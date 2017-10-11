package abstracts;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import map.Wall;

public class Mesh {
	
	public class Connection {
		int i1, i2;
		
		public Connection(int i1, int i2) {
			this.i1 = i1;
			this.i2 = i2;
		}
		
		public int getI1() {
			return i1;
		}
		
		public int getI2() {
			return i2;
		}
	}
	
	ArrayList<Point> points;
	ArrayList<Connection> connections;
	
	public Mesh(ArrayList<Wall> walls) {
		points = new ArrayList<>();
		addPoints(walls);
		
		connections = new ArrayList<>();
		findConnections(walls);
	}
	
	void addPoints(ArrayList<Wall> walls) {
		for(Wall w: walls) {
			for(int i = 0; i < w.npoints; i++) {
				Point p = new Point(w.xpoints[i], w.ypoints[i], w, i);
				addPoint(walls, p);
			}
		}
	}
	
	void addPoint(ArrayList<Wall> walls, Point p) {
		for(Wall w: walls) {
			if(p.wall != w && w.contains(p.x, p.y)){
				return;
			}
		}
		points.add(p);
	}
	
	void findConnections(ArrayList<Wall> walls) {
		for(int i = 0; i < points.size() - 1; i++) {
			Point p1 = points.get(i);
			for(int j = i+1; j < points.size(); j++) {
				Point p2 = points.get(j);
				
				if(connectionIsUseful(p1, p2, walls)) {
					connections.add(new Connection(i, j));
				}
			}
		}
	}
	
	boolean connectionIsUseful(Point p1, Point p2, ArrayList<Wall> walls) {
		if(p1.wall == p2.wall) {
			int diff = Math.abs(p1.wallIndex - p2.wallIndex);
			if(diff == 1 || diff == p1.wall.npoints-1) {
				return true;
			}
		}
		
		if(!p1.sees(p2, walls)) {
			return false;
		}
		
		//Connection vector (p1 -> p2)
		Vector c = new Vector(p2.x - p1.x, p2.y - p1.y).normalize();
		
		Wall w1 = p1.wall;
		int i1 = p1.wallIndex;
		int i1_1 = (p1.wallIndex + 1)%w1.npoints;
		int i1_2 = (p1.wallIndex + w1.npoints - 1)%w1.npoints;

		Vector v1_1 = new Vector(w1.xpoints[i1_1]-w1.xpoints[i1], w1.ypoints[i1_1]-w1.ypoints[i1]).normalize();
		Vector v1_2 = new Vector(w1.xpoints[i1_2]-w1.xpoints[i1], w1.ypoints[i1_2]-w1.ypoints[i1]).normalize();

		double s1_1 = v1_1.cross(c);
		double s1_2 = v1_2.cross(c);
		
		if(s1_1*s1_2 < 0) {
			return false;
		}
		
		Wall w2 = p2.wall;
		int i2 = p2.wallIndex;
		int i2_1 = (p2.wallIndex + 1)%w2.npoints;
		int i2_2 = (p2.wallIndex + w2.npoints - 1)%w2.npoints;

		Vector v2_1 = new Vector(w2.xpoints[i2_1]-w2.xpoints[i2], w2.ypoints[i2_1]-w2.ypoints[i2]).normalize();
		Vector v2_2 = new Vector(w2.xpoints[i2_2]-w2.xpoints[i2], w2.ypoints[i2_2]-w2.ypoints[i2]).normalize();

		double s2_1 = v2_1.cross(c);
		double s2_2 = v2_2.cross(c);
		
		return s2_1*s2_2 >= 0;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		for(Connection c: connections) {
			g.drawLine((int)points.get(c.i1).x, (int)points.get(c.i1).y, (int)points.get(c.i2).x, (int)points.get(c.i2).y);
		}
		
		g.setColor(Color.YELLOW);
		for(Point p: points) {
			g.fillOval((int)p.x-2, (int)p.y-2, 4, 4);
		}
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}
}
