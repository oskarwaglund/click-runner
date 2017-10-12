package units;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import abstracts.Mesh;
import abstracts.Node;
import abstracts.Point;
import map.Colors;
import map.Wall;

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

	public abstract int visionRange();

	protected abstract void paintUnit(Graphics g);

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

		this.color = Colors.getTeamColor(team);
		if (color == null) {
			color = Color.CYAN;
		}

		this.path = new ArrayDeque<>();
	}

	public void setPath(Point target, Mesh mesh, ArrayList<Wall> walls, boolean stack) {
		if (!stack) {
			path.clear();
		}

		ArrayList<Node> nodes = new ArrayList<>();
		for (Point p : mesh.getPoints()) {
			nodes.add(new Node(p.getX(), p.getY()));
		}

		for (Mesh.Connection c : mesh.getConnections()) {
			Node n1 = nodes.get(c.getI1());
			Node n2 = nodes.get(c.getI2());

			n1.addNeighbor(n2);
			n2.addNeighbor(n1);
		}

		Node start;

		start = new Node(x, y);

		Node goal = null;
		int tries = 0;
		while (goal == null && tries < 5) {
			double radius = Math.random();
			radius *= radius * 50;
			double angle = Math.random() * 2 * Math.PI;
			double x = target.getX() + Math.cos(angle) * radius;
			double y = target.getY() + Math.sin(angle) * radius;

			boolean collides = false;
			for (Wall w : walls) {
				if (w.contains(x, y)) {
					collides = true;
					break;
				}
			}
			if(!collides) {
				goal = new Node(x, y);
			} else {
				tries++;
			}
		}
		if (goal == null) {
			goal = new Node(target.getX(), target.getY());
		}

		Point startPoint = new Point(start.getX(), start.getY());
		Point goalPoint = new Point(goal.getX(), goal.getY());

		if (startPoint.sees(goalPoint, walls)) {
			path.add(goalPoint);
			return;
		}

		boolean goalReachable = false;
		for (Node n : nodes) {
			Point nodePoint = new Point(n.getX(), n.getY());
			if (nodePoint.sees(startPoint, walls)) {
				start.addNeighbor(n);
			}
			if (nodePoint.sees(goalPoint, walls)) {
				n.addNeighbor(goal);
				goalReachable = true;
			}
		}

		if (!goalReachable) {
			return;
		}
		path.addAll(aStar(start, goal));
	}

	ArrayDeque<Point> aStar(Node start, Node goal) {
		ArrayDeque<Point> path = new ArrayDeque<>();

		Set<Node> open = new TreeSet<>();
		Set<Node> closed = new TreeSet<>();

		start.setgScore(0);
		start.setfScore(Node.distance(start, goal));
		open.add(start);
		while (!open.isEmpty()) {
			Node current = null;
			double lowestFScore = Double.MAX_VALUE;
			for (Node n : open) {
				if (current == null || n.getfScore() < lowestFScore) {
					current = n;
					lowestFScore = n.getfScore();
				}
			}

			if (current.compareTo(goal) == 0) {
				Node n = goal;
				while (n != start) {
					path.addFirst(new Point(n.getX(), n.getY()));
					n = n.getBestFrom();
				}
			}

			open.remove(current);
			closed.add(current);

			for (Node neighbor : current.getNeighbors()) {
				if (closed.contains(neighbor)) {
					continue;
				}

				if (!open.contains(neighbor)) {
					open.add(neighbor);
				}

				double newGScore = current.getgScore() + Node.distance(current, neighbor);
				if (newGScore <= neighbor.getgScore()) {
					neighbor.setBestFrom(current);
					neighbor.setgScore(newGScore);
					neighbor.setfScore(neighbor.getgScore() + Node.distance(neighbor, goal));
				}
			}
		}
		return path;
	}

	public void updatePath(Mesh mesh, ArrayList<Wall> walls) {
		double pathX = x;
		double pathY = y;

		for (Point p : path) {
			Line2D.Double line = new Line2D.Double(pathX, pathY, p.getX(), p.getY());
			for (Wall w : walls) {
				if (w.collide(line)) {
					Point target = path.removeLast();
					path.clear();
					setPath(target, mesh, walls, false);
					return;
				}
			}
		}
	}

	public void step() {
		if (isDead()) {
			deadCounter++;
			return;
		}
		if (attackTarget != null) {
			if (attackTarget.isDead()) {
				attackTarget = null;
			} else {
				attack();
			}
		} else if (path.size() > 0) {
			Point p = path.getFirst();
			double distanceToPoint = p.distanceTo(new Point(this.x, this.y));
			if (distanceToPoint >= speed()) {
				x += (p.getX() - x) / distanceToPoint * speed();
				y += (p.getY() - y) / distanceToPoint * speed();
			} else {
				x = p.getX();
				y = p.getY();
				path.removeFirst();
			}
		}
	}

	public void attack() {
		double distance = distanceTo(attackTarget);
		if (distance <= attackRange()) {
			if (attackCounter < 0) {
				attackCounter = 0;
			}
		} else {
			x += (attackTarget.x - x) / distance * speed();
			y += (attackTarget.y - y) / distance * speed();
		}
		if (attackCounter >= 0) {
			attackCounter++;
			if (attackCounter == damageFrame()) {
				attackTarget.hp -= damage();
			}
			if (attackCounter >= attackDuration()) {
				attackCounter = -1;
			}
		}
	}

	public double distanceTo(Unit u) {
		return Math.hypot(u.x - x, u.y - y);
	}
	
	public double squaredDistanceTo(Unit u) {
		double dX = x - u.x;
		double dY = y - u.y;
		return dX*dX + dY*dY; 
	}

	public boolean sees(Unit u, ArrayList<Wall> walls) {
		Line2D.Double line = new Line2D.Double(x, y, u.x, u.y);
		for (Wall w : walls) {
			if (w.collide(line)) {
				return false;
			}
		}
		return true;
	}

	public void paint(Graphics g, boolean selected) {
		if (isDead()) {
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(),
					Math.max(0, 255 - 255 * deadCounter / deathTime()));
		} else {
			paintUnit(g);
			if(selected) {
				paintPath(g);
				paintHealthBar(g);
			}
		}
	}

	private void paintPath(Graphics g) {
		g.setColor(Colors.PATH);
		int lastX = (int) x;
		int lastY = (int) y;
		for (Point p : path) {
			g.drawLine(lastX, lastY, (int) p.getX(), (int) p.getY());
			lastX = (int) p.getX();
			lastY = (int) p.getY();
		}
	}

	private void paintHealthBar(Graphics g) {
		final int scale = 2;
		final int width = getMaxHP() * scale;
		final int height = 6;
		final int barWidth = hp * scale;

		final int barX = (int) (x - width / 2);
		final int barY = (int) (y - size() / 2 - 2 * height);
		g.setColor(Colors.HEALTH_BG);
		g.fillRect(barX, barY, width, height);
		g.setColor(Colors.HEALTH_FG);
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
		if (attackTarget != null) {
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
