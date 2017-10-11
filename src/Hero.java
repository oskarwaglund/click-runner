import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class Hero{

	static final int SIZE = 10;
	static final double JITTER = 0;
	static final double SPEED = 5.0;
	
	double x, y;
	double dx, dy; 
		
	ArrayDeque<Point> path;
	
	
	public Hero(double x, double y) {
		this.x = x;
		this.y = y;
		dx = 0;
		dy = 0;
		
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
		
		if(path.size() == 0) {
			start = new Node(x, y);
		} else {
			start = new Node(path.getLast().x, path.getLast().y);
		}
		
		Node goal = new Node(target.x, target.y);
		
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
			if(distanceToPoint >= SPEED) {
				x += (p.x - x)/distanceToPoint*SPEED;
				y += (p.y - y)/distanceToPoint*SPEED;
			} else {
				x = p.x;
				y = p.y;
				path.removeFirst();
			}
		}
	}
	
	public void paint(Graphics g, boolean showPath) {
		g.setColor(Color.RED);
		g.fillOval((int)(x - SIZE/2 + Math.random() * JITTER*2 - JITTER), (int)(y - SIZE/2 + Math.random() * JITTER*2 - JITTER), SIZE, SIZE);
		
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
		
	}
	
}
