import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public abstract class Unit {
	
	protected double x, y;

	private int hp;
	protected int deadCounter;
	
	protected int team;
	
	protected int attackCounter;	
	protected Unit attackTarget;
	
	protected Color color;
	
	ArrayDeque<Point> path;
	
	abstract double speed();
	abstract int size();
	
	abstract int getMaxHP();
	
	abstract int attackRange();
	abstract int attackDuration();
	abstract int damageFrame();
	abstract int damage();
	abstract int deathTime();
	
	abstract int visionRange();
	
	abstract void paint(Graphics g, boolean showPath, boolean selected);
	
	static Map<Integer, Color> TEAM_COLORS;
	static {
		TEAM_COLORS = new TreeMap<>();
		TEAM_COLORS.put(1, Color.RED);
		TEAM_COLORS.put(2, Color.BLUE);
	}
	public Unit(double x, double y) {
		this(x, y, 1);
	}
	
	public Unit(double x, double y, int team) {
		this.x = x;
		this.y = y;
		
		this.hp = getMaxHP();
		this.deadCounter = 0;
		
		this.team = team;
		
		this.attackCounter = -1;
		this.attackTarget = null;
		
		this.color = TEAM_COLORS.get(team);
		if(color == null) {
			color = Color.CYAN;
		}
		
		this.path = new ArrayDeque<>();
	}
		
	public void setPath(Point target, Mesh mesh, ArrayList<Wall> walls, boolean stack) {
		if(!stack) {
			path.clear();
		}
		
		ArrayList<Node> nodes = new ArrayList<>();
		for(Point p: mesh.points) {
			nodes.add(new Node(p.x, p.y));
		}
		
		for(Mesh.Connection c: mesh.connections) {
			Node n1 = nodes.get(c.i1);
			Node n2 = nodes.get(c.i2);
			
			n1.addNeighbor(n2);
			n2.addNeighbor(n1);
		}
		
		Node start;
		
		start = new Node(x, y);
		
		Node goal = null;
		int tries = 0;
		while(goal == null && tries < 5) {
			double radius = Math.random();
			radius *= radius * 20;
			double angle = Math.random() * 2 * Math.PI;
			double x = target.x + Math.cos(angle)*radius;
			double y = target.y + Math.sin(angle)*radius;
			
			for(Wall w: walls) {
				if(w.contains(x, y)) {
					continue;
				}
			}
			goal = new Node(x, y);
		}
		if(goal == null) {
			goal = new Node(target.x, target.y);
		}
		
		
		Point startPoint = new Point(start.x, start.y);
		Point goalPoint = new Point(goal.x, goal.y);
		
		if(startPoint.sees(goalPoint, walls)) {
			path.add(goalPoint);
			return;
		}
		
		boolean goalReachable = false;
		for(Node n: nodes) {
			Point nodePoint = new Point(n.x, n.y);
			if(nodePoint.sees(startPoint, walls)) {
				start.addNeighbor(n);
			}
			if(nodePoint.sees(goalPoint, walls)) {
				n.addNeighbor(goal);
				goalReachable = true;
			}
		}
		
		if(!goalReachable) {
			return;
		}
		path.addAll(aStar(start, goal));
	}
	
	ArrayDeque<Point> aStar(Node start, Node goal) {
		ArrayDeque<Point> path = new ArrayDeque<>();
		
		Set<Node> open = new TreeSet<>();
		Set<Node> closed = new TreeSet<>();
		
		start.gScore = 0;
		start.fScore = Node.distance(start, goal);
		open.add(start);
		while(!open.isEmpty()) {
			Node current = null;
			double lowestFScore = Double.MAX_VALUE;
			for(Node n: open) {
				if(current == null || n.fScore < lowestFScore) {
					current = n;
					lowestFScore = n.fScore;
				}
			}
			
			if(current.compareTo(goal) == 0) {
				Node n = goal;
				while(n != start) {
					path.addFirst(new Point(n.x, n.y));
					n = n.bestFrom;
				}
			}
			
			open.remove(current);
			closed.add(current);
			
			for(Node neighbor: current.neighbors) {
				if(closed.contains(neighbor)) {
					continue;
				}
				
				if(!open.contains(neighbor)) {
					open.add(neighbor);
				}
				
				double newGScore = current.gScore + Node.distance(current, neighbor);
				if(newGScore <= neighbor.gScore) {
					neighbor.bestFrom = current;
					neighbor.gScore = newGScore;
					neighbor.fScore = neighbor.gScore + Node.distance(neighbor, goal);
				}
			}
		}
		return path;
	}
	
	public void updatePath(Mesh mesh, ArrayList<Wall> walls) {
		double pathX = x;
		double pathY = y;
		
		for(Point p: path) {
			Line2D.Double line = new Line2D.Double(pathX, pathY, p.x, p.y);
			for(Wall w: walls) {
				if(w.collide(line)) {
					Point target = path.removeLast();
					path.clear();
					setPath(target, mesh, walls, false);
					return;
				}
			}
		}
	}
	
	public void step() {
		if(isDead()) {
			deadCounter++;
			return;
		}
		if(attackTarget != null) {
			if(attackTarget.isDead()) {
				attackTarget = null;
			} else {
				attack();
			}
		} else if(path.size() > 0) {
			Point p = path.getFirst();
			double distanceToPoint = p.distanceTo(new Point(this.x, this.y));
			if(distanceToPoint >= speed()) {
				x += (p.x - x)/distanceToPoint*speed();
				y += (p.y - y)/distanceToPoint*speed();
			} else {
				x = p.x;
				y = p.y;
				path.removeFirst();
			}
		}
	}
	
	public void attack() {
		double distance = distanceTo(attackTarget);
		if(distance <= attackRange()) {
			if(attackCounter < 0) {
				attackCounter = 0;
			}
		} else {
			x += (attackTarget.x - x)/distance*speed();
			y += (attackTarget.y - y)/distance*speed(); 
		}
		if(attackCounter >= 0) {
			attackCounter++;
			if(attackCounter == damageFrame()) {
				attackTarget.hp -= damage();
			}
			if(attackCounter >= attackDuration()) {
				attackCounter = -1;
			}
		}		
	}
	
	public double distanceTo(Unit u) {
		return Math.hypot(u.x-x, u.y-y);
	}
	
	public boolean sees(Unit u, ArrayList<Wall> walls) {
		Line2D.Double line = new Line2D.Double(x,  y, u.x, u.y);
		for(Wall w: walls) {
			if(w.collide(line)) {
				return false;
			}
		}
		return true;
	}
	
	protected void paintHealthBar(Graphics g) {
		final int scale = 2;
		final int width = getMaxHP()*scale;
		final int height = 6;
		final int barWidth = hp*scale;

		final int barX = (int)(x - width/2);
		final int barY = (int)(y - size()/2 - 2*height);
		g.setColor(Color.RED);
		g.fillRect(barX, barY, width, height);
		g.setColor(Color.GREEN);
		g.fillRect(barX, barY, barWidth, height);
	}
		
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getHp() {
		return hp;
	}
	public int getTeam() {
		return team;
	}
	public int getAttackCounter() {
		return attackCounter;
	}
	public void setAttackTarget(Unit attackTarget) {
		if(attackTarget != null) {
			this.attackTarget = attackTarget;
			attackCounter = -1;
		}
	}
	public Unit getAttackTarget() {
		return attackTarget;
	}
	public ArrayDeque<Point> getPath() {
		return path;
	}
	public boolean isDead() {
		return hp <= 0;
	}
	public boolean remove() {
		return deadCounter >= deathTime();
	}
}
