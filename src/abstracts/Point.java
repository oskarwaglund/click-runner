package abstracts;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import map.Wall;

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
		
		//Create vector of length 1 from this point to p
		double vX = p.x - x;
		double vY = p.y - y;
		
		//Normalize
		double vL = Math.hypot(vX, vY);
		vX /= vL;
		vY /= vL;
		
		Line2D.Double line = new Line2D.Double(x+vX, y+vY, p.x-vX, p.y-vY);
		
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

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Wall getWall() {
		return wall;
	}

	public int getWallIndex() {
		return wallIndex;
	}
}
