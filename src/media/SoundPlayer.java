package media;

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
		String resourceName;
		long nextPlayTime;
		
		Sound(String resourceName){
			this.resourceName = resourceName;
		}
		
		void play() {
			if(System.currentTimeMillis() >= nextPlayTime) {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getClassLoader().getResource(resourceName));
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
		addSound(SoundEnum.DRONE_HIT, "hit.wav");
		addSound(SoundEnum.SHOOTER_SHOT, "shot.wav");
		addSound(SoundEnum.THEME, "waterloo.wav");
		addSound(SoundEnum.BOMBER_BLAST, "blast.wav");
	}
	
	public static void addSound(SoundEnum sound, String fileName) {
		sounds.put(sound, new Sound(fileName));
	}

	public static void playSound(SoundEnum sound) {
		if (sounds.containsKey(sound)) {
			sounds.get(sound).play();
		}
	}
}
