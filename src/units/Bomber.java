package units;

import java.awt.Color;
import java.awt.Graphics;

import abstracts.Clock;
import abstracts.Vector;
import media.SoundPlayer;

public class Bomber extends Unit {

	private static final int MAX_HP = 50;
	private static final int SPEED = 2; 
	private static final int SIZE = 14;
	
	private static final int VISION_RANGE = 100;
	
	private static final int ATTACK_RANGE = 60;
	private static final int ATTACK_DURATION = 1000/Clock.FRAME_LENGTH; 
	private static final int DAMAGE_FRAME = 10;
	private static final int DAMAGE = 2;
	private static final int SPLASH = 50;
	private static final int DEATH_TIME = 4000/Clock.FRAME_LENGTH; 
	
	private int splashX, splashY;
	private static final int DETONATION_DELAY = 10;
	
	public Bomber(double x, double y) {
		super(x, y);
	}
	
	public Bomber(double x, double y, int team) {
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
		SoundPlayer.playSound(SoundPlayer.SoundEnum.BOMBER_BLAST);
	}
	
	@Override
	void attackTargetAcquired() {
		splashX = (int)attackTarget.getX();
		splashY = (int)attackTarget.getY();
	}

	@Override
	protected void paintUnit(Graphics g) {
		g.setColor(color);
		g.fillOval((int)(x - size()/2), (int)(y - size()/2), size(), size());
		
		if(attackCounter >= 0) {
			if(attackCounter <= DAMAGE_FRAME) {
				Vector v = new Vector(splashX - this.x, splashY-this.y).multiply((double)attackCounter / DAMAGE_FRAME);
				g.setColor(Color.BLACK);
				g.fillOval((int)(x + v.getX() - 2), (int)(y + v.getY() - 2), 4, 4);
			} else if(attackCounter <= DAMAGE_FRAME + DETONATION_DELAY){
				double fraction = (double)(attackCounter - DAMAGE_FRAME)/DETONATION_DELAY;
				int radius = (int)(SPLASH*fraction);
				int alpha = (int)((1-fraction)*100);
				
				g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
				g.fillOval(splashX - radius, splashY - radius, radius*2, radius*2);
			}
		}
	}

}
