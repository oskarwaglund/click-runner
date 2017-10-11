
public class Clock {
	
	public static final int FRAME_LENGTH = 20; //ms 
	
	private static long clock = 0;
	
	static long getClock() {
		return clock;
	}
	
	static void tick() {
		++clock;
	}
}
