package de.jablab.sebschlicht.android.kits;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Native;

import de.jablab.sebschlicht.android.kits.commands.Command;
import de.jablab.sebschlicht.android.kits.commands.IntroType;
import de.jablab.sebschlicht.android.kits.commands.PlayCommand;
import de.jablab.sebschlicht.android.kits.commands.SetVolumeCommand;
import de.jablab.sebschlicht.android.kits.player.SeriesPlayer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class KitsServer
        implements Handler<io.vertx.core.datagram.DatagramPacket> {

    private final static Logger LOG = LoggerFactory.getLogger(KitsServer.class);

    private static final int KITS_SERVER_PORT = 61010;

    private DatagramSocket socket;

    private SeriesPlayer player;

    public KitsServer(
            KitsServerConfig config) {
        LOG.info("Intro audio files directory: "
                + config.getAudioIntroDirectory());
        LOG.info("Intro video files directory: "
                + config.getVideoIntroDirectory());

        // create UDP socket for listening
        Vertx vertx = Vertx.vertx();
        socket = vertx.createDatagramSocket(
                new DatagramSocketOptions().setBroadcast(true));

        this.player = new SeriesPlayer(config.getAudioIntroDirectory(),
                config.getVideoIntroDirectory());
    }

    public void startListening() {
        socket.listen(KITS_SERVER_PORT, "0.0.0.0", asyncResult -> {
            if (asyncResult.succeeded()) {
                LOG.info("Listening for KiTS applications on "
                        + socket.localAddress() + "...");
                socket.handler(this);
            } else {
                LOG.warn("Failed to listen for next packet!",
                        asyncResult.cause());
            }
        });
    }

    @Override
    public void handle(io.vertx.core.datagram.DatagramPacket event) {
        Buffer data = event.data();
        if (data == null) {
            return;
        }
        String content = data.toString();
        if (content == null) {
            return;
        }

        // TODO make this async, the string SHOULD NOT but potentially COULD be large
        Command command = Command.parseStringQuietly(content);
        if (command == null) {
            // unexpected command
            LOG.warn("Unexpected command received: " + content);
            return;
        }

        switch (command.getType()) {
            case REGISTER:
                LOG.debug("Registering host \"" + event.sender() + "\"...");
                Buffer buffer =
                        Buffer.buffer(Command.SERVER_SEARCH_RESPONSE_STRING);
                socket.send(buffer, event.sender().port(),
                        event.sender().host(), asyncResult -> {
                            LOG.info("KiTS application on host \""
                                    + event.sender().host()
                                    + "\" has registered successfully.");
                        });
                break;

            case PLAY:
                PlayCommand play = (PlayCommand) command;
                LOG.info("playing: " + play.getName() + " ("
                        + play.getIntroType() + ")");
                if (play.getIntroType() == IntroType.FULL) {
                    try {
                        player.playVideoIntro(play.getName());
                    } catch (FileNotFoundException e) {
                        LOG.warn("Video intro for series \"" + play.getName()
                                + "\" is missing: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        LOG.error("Failed to play video intro for series \""
                                + play.getName() + "\"!", e);
                    }
                } else {
                    try {
                        player.playAudioIntro(play.getName());
                    } catch (FileNotFoundException e) {
                        LOG.warn("Audio intro for series \"" + play.getName()
                                + "\" is missing: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        LOG.error("Failed to play audio intro for series \""
                                + play.getName() + "\"!", e);
                    }
                }
                break;

            case SET_VOLUME:
                SetVolumeCommand setVolume = (SetVolumeCommand) command;
                LOG.debug("Setting volume to " + setVolume.getVolume() + "...");
                player.setVolume(setVolume.getVolume());
                LOG.info("Volume has been set to " + setVolume.getVolume()
                        + ".");
                break;

            case STOP:
                LOG.debug("Stopping...");
                player.stop();
                LOG.info("Playback has been stopped.");
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        // load server configuration
        KitsServerConfig config = new KitsServerConfig("config.properties");

        // link server to VLC library
        // sudo apt-get install libvlc-dev
        // DO NOT mount /tmp with noexec
        // sudo mount -o remount,exec /tmp
        System.setProperty("jna.nosys", "true");
        try {
            new NativeDiscovery().discover();
            System.out.println("Connected to VLC "
                    + LibVlc.INSTANCE.libvlc_get_version() + ".");
        } catch (Exception e) {
            System.err.println(
                    "Failed to link VLC library! See exception below for details:");
            e.printStackTrace();
            return;
        }
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        // start server
        KitsServer server = new KitsServer(config);
        server.startListening();
    }
}
