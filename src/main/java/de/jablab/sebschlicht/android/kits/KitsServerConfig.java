package de.jablab.sebschlicht.android.kits;

import java.io.File;

import de.jablab.sebschlicht.ApplicationConfig;

public class KitsServerConfig extends ApplicationConfig {

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
