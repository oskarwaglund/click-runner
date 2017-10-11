package units;
import java.awt.Color;
import java.awt.Graphics;

import abstracts.Clock;
import abstracts.Point;
import abstracts.Vector;

public class Drone extends Unit{

	private static final int MAX_HP = 20;
	private static final int SPEED = 5; 
	private static final int SIZE = 10;
	
	private static final int VISION_RANGE = 100;
	
	private static final int ATTACK_RANGE = 10;
	private static final int ATTACK_DURATION = 1000/Clock.FRAME_LENGTH; //1 second
	private static final int DAMAGE_FRAME = 10;
	private static final int DAMAGE = 3;
	private static final int DEATH_TIME = 20;
	
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
	public int visionRange() {
		return VISION_RANGE;
	}
	
	@Override
	int deathTime() {
		return 1000/Clock.FRAME_LENGTH*2; //2 seconds
	}

	public void paint(Graphics g, boolean showPath, boolean selected) {
		double x = this.x;
		double y = this.y;
		
		if(!isDead() && attackTarget != null && attackCounter < DAMAGE_FRAME*2) {
			Vector v = new Vector(this, attackTarget).multiply(0.5*Math.sin((double)attackCounter / DAMAGE_FRAME / 2 * Math.PI));
			x += v.getX();
			y += v.getY();
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
			g.setColor(Color.YELLOW);
			g.fillOval((int)x-2, (int)(y-size()-10), 4, 4);
		}
		
		paintHealthBar(g);
	}
}
