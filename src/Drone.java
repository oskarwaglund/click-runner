public class Drone extends Unit{

	private static final int MAX_HP = 20;
	private static final int SPEED = 5; 
	private static final int SIZE = 10;
	
	public Drone(double x, double y) {
		super(x, y);
	}
	
	int getMaxHP() {
		return MAX_HP;
	}

	@Override
	double speed() {
		return SPEED;
	}

	@Override
	int size() {
		return SIZE;
	}
}
