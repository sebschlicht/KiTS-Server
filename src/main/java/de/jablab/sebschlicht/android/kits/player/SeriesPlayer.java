package de.jablab.sebschlicht.android.kits.player;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

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

    public void playAudioIntro(String name) throws FileNotFoundException {
        File audioIntro = new File(audioIntroDir, name + ".ogg");
        // TODO wrap FNF in SeriesFNF exception?
        player.play(audioIntro);
    }

    public void playVideoIntro(String name) throws FileNotFoundException {
        File videoIntro = new File(videoIntroDir, name + ".mp4");
        // TODO wrap FNF in SeriesFNF exception?
        player.play(videoIntro);
    }

    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    public void stop() {
        player.stop();
    }

    public void close() {
        player.close();
    }
}
