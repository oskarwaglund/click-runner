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
								
				Point p = new Point(w.xpoints[i] + x, w.ypoints[i] + y, w);
				
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
				
				if(p1.sees(p2, walls)) {
					connections.add(new Connection(i, j));
				}
			}
		}
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
