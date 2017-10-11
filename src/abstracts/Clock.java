package abstracts;

public class Clock {
	
	public static final int FRAME_LENGTH = 33; //ms 
	
	private static long clock = 0;
	
	public static long getClock() {
		return clock;
	}
	
	public static void tick() {
		++clock;
	}
}
