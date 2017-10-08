import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Point {

	double x, y;
	
	Wall wall;
	
	public Point(double x, double y) {
		this(x, y, null);
	}
	
	public Point(double x, double y, Wall wall) {
		this.x = x;
		this.y = y;
		this.wall = wall;
	}
	
	public double distanceTo(Point p) {
		return Math.hypot(p.x - x, p.y - y);
	}
	
	public boolean sees(Point p, ArrayList<Wall> walls) {
		Line2D.Double line = new Line2D.Double(x, y, p.x, p.y);
		for(Wall w: walls) {
			if(w.collide(line)) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
