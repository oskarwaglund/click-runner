package units;
import java.awt.Graphics;

import abstracts.Clock;
import abstracts.Vector;
import media.SoundPlayer;

public class Drone extends Unit{

	private static final int MAX_HP = 20;
	private static final int SPEED = 5; 
	private static final int SIZE = 10;
	
	private static final int VISION_RANGE = 100;
	
	private static final int ATTACK_RANGE = 10;
	private static final int ATTACK_DURATION = 1000/Clock.FRAME_LENGTH; 
	private static final int DAMAGE_FRAME = 10;
	private static final int DAMAGE = 3;
	private static final int SPLASH = 0;
	private static final int DEATH_TIME = 2000/Clock.FRAME_LENGTH; 
	
	public Drone(double x, double y) {
		super(x, y);
	}
	
	public Drone(double x, double y, int team) {
		super(x, y, team);
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

	@Override
	int attackRange() {
		return ATTACK_RANGE;
	}

	@Override
	int attackDuration() {
		return ATTACK_DURATION;
	}

	@Override
	int damageFrame() {
		return DAMAGE_FRAME;
	}

	@Override
	int damage() {
		return DAMAGE;
	}
	
	@Override
	public int splash() {
		return SPLASH;
	}

	@Override
	public int visionRange() {
		return VISION_RANGE;
	}
	
	@Override
	int deathTime() {
		return DEATH_TIME;
	}
	
	@Override
	void attackFunction(Unit u) {
		u.hp -= DAMAGE;
		SoundPlayer.playSound(SoundPlayer.SoundEnum.DRONE_HIT);
	}
	
	@Override
	void attackTargetAcquired() {
		
	}

	public void paintUnit(Graphics g) {
		double x = this.x;
		double y = this.y;
		
		if(attackTarget != null && attackCounter < DAMAGE_FRAME*2) {
			Vector v = new Vector(this, attackTarget).multiply(0.5*Math.sin((double)attackCounter / DAMAGE_FRAME / 2 * Math.PI));
			x += v.getX();
			y += v.getY();
		}

		g.setColor(color);
		g.fillOval((int)(x - size()/2), (int)(y - size()/2), size(), size());		
	}
}
