package de.jablab.sebschlicht.android.kits.player;

import java.awt.Canvas;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VlcPlayer {

    private int volume = 100;

    private MediaPlayer player;

    public VlcPlayer(
            JFrame mainFrame,
            Canvas videoSurface) {
        String[] libvlcArgs = {
            "--no-video-title-show"
        };
        MediaPlayerFactory mediaPlayerFactory =
                new MediaPlayerFactory(libvlcArgs);
        EmbeddedMediaPlayer player = mediaPlayerFactory.newEmbeddedMediaPlayer(
                new DefaultFullScreenStrategy(mainFrame));

        String[] standardMediaOptions = {};
        player.setStandardMediaOptions(standardMediaOptions);

        player.setVideoSurface(
                mediaPlayerFactory.newVideoSurface(videoSurface));
        mediaPlayerFactory.release();

        this.player = player;
    }

    public void play(File file, String[] options) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(
                    "File \"" + file.getAbsolutePath() + "\" is missing!");
        }
        player.setVolume(volume);
        if (!player.playMedia(file.getAbsolutePath(), options)) {
            throw new IllegalStateException(
                    "Failed to play file \"" + file.getAbsolutePath() + "\"!");
        }
    }

    public void play(File file) throws FileNotFoundException {
        play(file, new String[] {});
    }

    public void setVolume(int volume) {
        int newVolume = Math.max(0, volume);
        newVolume = Math.min(100, volume);
        this.volume = newVolume;
        player.setVolume(newVolume);
    }

    public void stop() {
        player.stop();
    }

    public void close() {
        player.release();
    }
}
