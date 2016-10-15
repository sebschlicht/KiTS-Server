package de.jablab.sebschlicht.android.kits;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;

import com.sun.jna.Native;

import de.jablab.sebschlicht.BroadcastReceiver;
import de.jablab.sebschlicht.android.kits.commands.Command;
import de.jablab.sebschlicht.android.kits.commands.IntroType;
import de.jablab.sebschlicht.android.kits.commands.PlayCommand;
import de.jablab.sebschlicht.android.kits.commands.RegisterCommand;
import de.jablab.sebschlicht.android.kits.commands.SetVolumeCommand;
import de.jablab.sebschlicht.android.kits.commands.StopCommand;
import de.jablab.sebschlicht.android.kits.player.SeriesPlayer;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class KitsServer implements BroadcastReceiver.ReceiverCallback {

	private static final int KITS_SERVER_PORT = 61010;

	private BroadcastReceiver broadcastReceiver;

	private SeriesPlayer player;

	private Thread clientSearch;

	public KitsServer(KitsServerConfig config) {
		this.broadcastReceiver = new BroadcastReceiver(this, KITS_SERVER_PORT);

		this.clientSearch = new Thread(this.broadcastReceiver);
		this.clientSearch.start();

		this.player = new SeriesPlayer(config.getAudioIntroDirectory(), config.getVideoIntroDirectory());
	}

	@Override
	public void handleRequest(DatagramPacket request) {
		String message;
		try {
			message = new String(request.getData(), "UTF-8");
			message = message.trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		Command command = Command.parseString(message);
		if (command instanceof RegisterCommand) {
			this.broadcastReceiver.sendResponse(request.getAddress(), request.getPort(),
					Command.SERVER_SEARCH_RESPONSE);
			System.out.println("client registered (" + request.getAddress() + ")");
		} else if (command instanceof PlayCommand) {
			PlayCommand play = (PlayCommand) command;
			System.out.println("playing: " + play.getName() + " (" + play.getType() + ")");
			if (play.getType() == IntroType.FULL) {
				System.out.println(this.player.playVideoIntro(play.getName()));
			} else {
				System.out.println(this.player.playAudioIntro(play.getName()));
			}
		} else if (command instanceof SetVolumeCommand) {
			SetVolumeCommand setVolume = (SetVolumeCommand) command;
			System.out.println("volume: " + setVolume.getVolume());
			this.player.setVolume(setVolume.getVolume());
		} else if (command instanceof StopCommand) {
			System.out.println("stop");
			this.player.stop();
		} else {
			System.err.println("unknown message:\n" + message + "\n: " + command);
		}
	}

	public void shutdown() {
		this.clientSearch.interrupt();
	}

	public static void main(String[] args) throws IOException {
		KitsServerConfig config = new KitsServerConfig();
		config.loadFromResourcesFile("config.properties");

		System.setProperty("jna.nosys", "true");

		// sudo apt-get install libvlc-dev
		// DO NOT mount /tmp with noexec
		// sudo mount -o remount,exec /tmp
		try {
			new NativeDiscovery().discover();
			System.out.println("Connected to VLC " + LibVlc.INSTANCE.libvlc_get_version() + ".");
		} catch (Exception e) {
			System.err.println("Failed to link VLC library! See exception below for details:");
			e.printStackTrace();
			return;
		}

		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		KitsServer server = new KitsServer(config);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.shutdown();
				System.out.println("server shutted down");
			}
		});
	}
}
