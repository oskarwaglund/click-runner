public class Vector {
	double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
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
