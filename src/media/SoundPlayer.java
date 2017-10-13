package media;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	private static final int PLAY_DELAY = 100;
	
	public enum SoundEnum {
		DRONE_HIT,
		SHOOTER_SHOT,
		THEME,
		BOMBER_BLAST
	}

	private static class Sound {
		File file;
		long nextPlayTime;
		
		Sound(File file){
			this.file = file;
		}
		
		void play() {
			if(System.currentTimeMillis() >= nextPlayTime) {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream ais = AudioSystem.getAudioInputStream(file);
					clip.open(ais);
					clip.start();
					nextPlayTime = System.currentTimeMillis() + PLAY_DELAY;
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static Map<SoundEnum, Sound> sounds;
	static {
		sounds = new TreeMap<>();
		addSound(SoundEnum.DRONE_HIT, "sounds\\hit.wav");
		addSound(SoundEnum.SHOOTER_SHOT, "sounds\\shot.wav");
		addSound(SoundEnum.THEME, "sounds\\waterloo.wav");
		addSound(SoundEnum.BOMBER_BLAST, "sounds\\blast.wav");
	}
	
	public static void addSound(SoundEnum sound, String fileName) {
		sounds.put(sound, new Sound(new File(fileName)));
	}

	public static void playSound(SoundEnum sound) {
		if (sounds.containsKey(sound)) {
			sounds.get(sound).play();
		}
	}
}
