package de.jablab.sebschlicht.android.kits.player;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;

public class SeriesPlayer {

    private File audioIntroDir;

    private File videoIntroDir;

    private JFrame frame;

    private VlcPlayer player;

    public SeriesPlayer(
            File audioIntroDir,
            File videoIntroDir) {
        this.audioIntroDir = audioIntroDir;
        this.videoIntroDir = videoIntroDir;

        Canvas videoSurface = new Canvas();
        videoSurface.setBackground(Color.black);

        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.black);
        frame.add(videoSurface, BorderLayout.CENTER);
        player = new VlcPlayer(frame, videoSurface);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public boolean playAudioIntro(String name) {
        File audioIntro = new File(audioIntroDir, name + ".ogg");
        if (audioIntro.exists()) {
            player.play(audioIntro.getPath(), new String[] {});
            return true;
        } else {
            System.out.println("missing audio file: " + audioIntro.getPath());
            return false;
        }
    }

    public boolean playVideoIntro(String name) {
        File videoIntro = new File(videoIntroDir, name + ".mp4");
        if (videoIntro.exists()) {
            player.play(videoIntro.getPath(), new String[] {});
            return true;
        } else {
            System.out.println("missing video file: " + videoIntro.getPath());
            return false;
        }
    }

    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    public void stop() {
        player.stop();
    }
}
