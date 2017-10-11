public class Vector {
	double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Unit u1, Unit u2) {
		this.x = u2.x - u1.x;
		this.y = u2.y - u1.y;
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
	
}
