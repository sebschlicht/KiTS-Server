package de.jablab.sebschlicht.android.kits.player;

import java.awt.Canvas;

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
        EmbeddedMediaPlayer player =
                mediaPlayerFactory
                        .newEmbeddedMediaPlayer(new DefaultFullScreenStrategy(
                                mainFrame));

        String[] standardMediaOptions = {};
        player.setStandardMediaOptions(standardMediaOptions);

        player.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
        mediaPlayerFactory.release();

        this.player = player;
    }

    public void play(String filePath, String[] options) {
        player.playMedia(filePath, options);
        player.setVolume(volume);
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

    public void cleanUp() {
        player.release();
    }
}
