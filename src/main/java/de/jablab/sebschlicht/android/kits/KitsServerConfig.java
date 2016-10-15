package de.jablab.sebschlicht.android.kits;

import java.io.File;
import java.io.IOException;

import de.jablab.sebschlicht.ApplicationConfig;

public class KitsServerConfig extends ApplicationConfig {

    public KitsServerConfig(
            File file) throws IOException {
        super(file);
    }

    public KitsServerConfig(
            String resourceName) throws IOException {
        super(resourceName);
    }

    private File audioIntroDirectory;

    private File videoIntroDirectory;

    public File getAudioIntroDirectory() {
        return audioIntroDirectory;
    }

    public void setAudioIntroDirectory(File audioIntroDirectory) {
        this.audioIntroDirectory = audioIntroDirectory;
    }

    public File getVideoIntroDirectory() {
        return videoIntroDirectory;
    }

    public void setVideoIntroDirectory(File videoIntroDirectory) {
        this.videoIntroDirectory = videoIntroDirectory;
    }
}
