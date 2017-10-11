package units;

import java.awt.Color;
import java.awt.Graphics;

import abstracts.Clock;
import abstracts.Point;
import abstracts.Vector;

public class Shooter extends Unit{

	private static final int MAX_HP = 10;
	private static final int SPEED = 4; 
	private static final int SIZE = 8;
	
	private static final int VISION_RANGE = 130;
	
	private static final int ATTACK_RANGE = 100;
	private static final int ATTACK_DURATION = 1000/Clock.FRAME_LENGTH;
	private static final int DAMAGE_FRAME = 10;
	private static final int DAMAGE = 4;
	private static final int DEATH_TIME = 2000/Clock.FRAME_LENGTH;
	
	public Shooter(double x, double y) {
		super(x, y);
	}
	
	public Shooter(double x, double y, int team) {
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
	public int visionRange() {
		return VISION_RANGE;
	}
	
	@Override
	int deathTime() {
		return DEATH_TIME;
	}

	@Override
	public void paint(Graphics g, boolean showPath, boolean selected) {
		if(!isDead() && attackTarget != null && attackCounter < DAMAGE_FRAME) {
			final int bulletSize = 4;
			Vector v = new Vector(this, attackTarget).multiply((double)attackCounter/DAMAGE_FRAME);
			g.setColor(Color.BLACK);
			g.fillOval((int)(x + v.getX()-bulletSize/2), (int)(y + v.getY()-bulletSize/2), bulletSize, bulletSize);
		}

		if(isDead()) {
			Color teamColor = TEAM_COLORS.get(team);
			color = new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue(), Math.max(0, 255-255*deadCounter/DEATH_TIME));
		}
		
		g.setColor(color);
		g.fillOval((int)(x - size()/2), (int)(y - size()/2), size(), size());
		
		if(isDead()) {
			return;
		}
		if(showPath) {
			g.setColor(Color.GREEN);
			int lastX = (int)x;
			int lastY = (int)y;
			for(Point p: path) {
				g.drawLine(lastX, lastY, (int)p.getX(), (int)p.getY());
				lastX = (int)p.getX();
				lastY = (int)p.getY();
			}
		}
		
		if(selected) {
			paintHealthBar(g);
		}
	}

}
