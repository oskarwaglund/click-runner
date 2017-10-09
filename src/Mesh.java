import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Mesh {
	
	class Connection {
		int i1, i2;
		
		public Connection(int i1, int i2) {
			this.i1 = i1;
			this.i2 = i2;
		}
	}
	
	ArrayList<Point> points;
	ArrayList<Connection> connections;
	int cornerDistance = 1;
	
	public Mesh(ArrayList<Wall> walls) {
		points = new ArrayList<>();
		addPoints(walls);
		
		connections = new ArrayList<>();
		findConnections(walls);
	}
	
	void addPoints(ArrayList<Wall> walls) {
		for(Wall w: walls) {
			for(int i = 0; i < w.npoints; i++) {
				int i1 = (i - 1 + w.npoints)%w.npoints;
				int i2 = (i + 1)%w.npoints; 
				
				int x = (w.xpoints[i1] + w.xpoints[i2])/2 - w.xpoints[i];
				int y = (w.ypoints[i1] + w.ypoints[i2])/2 - w.ypoints[i];
				
				double length = Math.hypot(x, y);
				
				final int DIST = 4;
				x *= -DIST/length;
				y *= -DIST/length;
								
				Point p = new Point(w.xpoints[i] + x, w.ypoints[i] + y, w, i);
				
				addPoint(walls, p);
			}
		}
	}
	
	void addPoint(ArrayList<Wall> walls, Point p) {
		for(Wall w: walls) {
			if(w.contains(p.x, p.y)){
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
				
				if(p1.sees(p2, walls) && connectionIsUseful(p1, p2)) {
					connections.add(new Connection(i, j));
				}
			}
		}
	}
	
	boolean connectionIsUseful(Point p1, Point p2) {
		if(p1.wall == p2.wall) {
			int diff = Math.abs(p1.wallIndex - p2.wallIndex);
			return diff == 1 || diff == p1.wall.npoints-1;
		}
		
		//Connection vector (p1 -> p2)
		int cX = (int)(p2.x - p1.x);
		int cY = (int)(p2.y - p1.y);
		
		Wall w1 = p1.wall;
		int i1 = p1.wallIndex;
		int i1_1 = (p1.wallIndex + 1)%w1.npoints;
		int i1_2 = (p1.wallIndex + w1.npoints - 1)%w1.npoints;
		
		int s1_1 = (w1.xpoints[i1_1]-w1.xpoints[i1])*cY - cX*(w1.ypoints[i1_1]-w1.ypoints[i1]);
		int s1_2 = (w1.xpoints[i1_2]-w1.xpoints[i1])*cY - cX*(w1.ypoints[i1_2]-w1.ypoints[i1]);
		
		if(s1_1*s1_2 < 0) {
			return false;
		}
		
		Wall w2 = p2.wall;
		int i2 = p2.wallIndex;
		int i2_1 = (p2.wallIndex + 1)%w2.npoints;
		int i2_2 = (p2.wallIndex + w2.npoints - 1)%w2.npoints;
		
		int s2_1 = (w2.xpoints[i2_1]-w2.xpoints[i2])*cY - cX*(w2.ypoints[i2_1]-w2.ypoints[i2]);
		int s2_2 = (w2.xpoints[i2_2]-w2.xpoints[i2])*cY - cX*(w2.ypoints[i2_2]-w2.ypoints[i2]);
		
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
}
