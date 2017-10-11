import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public abstract class Unit {
	
	private final int MAX_HP = 0;
	
	double x, y;

	abstract double speed();
	abstract int size();
	abstract int getMaxHP();
	int hp;
	
	ArrayDeque<Point> path;
	
	public Unit(double x, double y) {
		this.x = x;
		this.y = y;
		
		hp = MAX_HP;
		
		path = new ArrayDeque<>();
	}
	
	/* Pathing */
	
	public void setPath(Point target, Mesh mesh, ArrayList<Wall> walls) {
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
			path.clear();
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
		path.clear();
		path.addAll(aStar(start, goal));
	}
	
	ArrayDeque<Point> aStar(Node start, Node goal) {
		ArrayDeque<Point> path = new ArrayDeque<>();
		
		Set<Node> open = new TreeSet<>();
		Set<Node> closed = new TreeSet<>();
		
		start.gScore = 0;
		start.fScore = distance(start, goal);
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
				
				double newGScore = current.gScore + distance(current, neighbor);
				if(newGScore <= neighbor.gScore) {
					neighbor.bestFrom = current;
					neighbor.gScore = newGScore;
					neighbor.fScore = neighbor.gScore + distance(neighbor, goal);
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
					setPath(target, mesh, walls);
					return;
				}
			}
		}
	}
	
	static double distance(Node n1, Node n2) {
		return Math.hypot(n1.x-n2.x, n1.y-n2.y);
	}
	
	public void step() {
		if(path.size() > 0) {
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
	
	public void paint(Graphics g, boolean showPath, boolean selected) {
		g.setColor(Color.RED);
		g.fillOval((int)(x - size()/2), (int)(y - size()/2), size(), size());
		
		if(showPath) {
			g.setColor(Color.GREEN);
			int lastX = (int)x;
			int lastY = (int)y;
			for(Point p: path) {
				g.drawLine(lastX, lastY, (int)p.x, (int)p.y);
				lastX = (int)p.x;
				lastY = (int)p.y;
			}
		}
		
		if(selected) {
			g.setColor(Color.YELLOW);
			g.fillOval((int)x-2, (int)(y-size()-10), 4, 4);
		}
		
	}
	
}
