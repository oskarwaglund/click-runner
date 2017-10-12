package abstracts;
import units.Unit;

public class Vector {
	double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Unit u1, Unit u2) {
		this.x = u2.getX() - u1.getX();
		this.y = u2.getY() - u1.getY();
	}
	
	public Vector(Point p1, Point p2) {
		this.x = p2.getX() - p1.getX();
		this.y = p2.getY() - p1.getY();
	}
	
	public Vector multiply(double a) {
		return new Vector(x*a, y*a);
	}
	
	public double scalar(Vector v) {
		return x*v.x + y*v.y;
	}
	
	public double cross(Vector v) {
		return x*v.y - v.x*y;
	}
	
	public double length() {
		return Math.hypot(x, y);
	}
	
	public Vector normalize() {
		double l = length();
		double xx = x/l;
		double yy = y/l;
		return new Vector(xx, yy);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
}
