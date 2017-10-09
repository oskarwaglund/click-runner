import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Point {

	double x, y;
	
	Wall wall;
	int wallIndex;
	
	public Point(double x, double y) {
		this(x, y, null, -1);
	}
	
	public Point(double x, double y, Wall wall, int wallIndex) {
		this.x = x;
		this.y = y;
		this.wall = wall;
		this.wallIndex = wallIndex;
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
