import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;

public class Wall extends Polygon{
	
	public Wall(int[] xpoints, int[] ypoints) {
		super(xpoints, ypoints, xpoints.length);
		
		int sum = 0;
		for(int i = 0; i < npoints; i++) {
			sum += (xpoints[(i+1)%npoints] - xpoints[i])*(ypoints[(i+1)%npoints] + ypoints[i]);
		}
		
		//Reverse
		if(sum > 0) {
			for(int i = 0; i < npoints / 2; i++){
			    int tempX = xpoints[i];
			    int tempY = ypoints[i];
			    
			    xpoints[i] = xpoints[npoints - i - 1];
			    ypoints[i] = ypoints[npoints - i - 1];
			    
			    xpoints[npoints - i - 1] = tempX;
			    ypoints[npoints - i - 1] = tempY;
			}
		}
	}
	
	public Wall(int x, int y, int width, int height) {
		this(new int[] {x, x+width, x+width, x}, new int[] {y, y, y+height, y+height});
	}
	
	public Wall(int cx, int cy, int radius) {
		super();
		npoints = (int)(2*radius*Math.PI/10);
		xpoints = new int[npoints];
		ypoints = new int[npoints];
		for(int i = 0; i < npoints; i++) {
			double angle = 2 * Math.PI * i / npoints;
			xpoints[i] = cy + (int)(Math.cos(angle) * radius);
			ypoints[i] = cx + (int)(Math.sin(angle) * radius);
		}
	}
	
	public boolean collide(Line2D.Double line) {
		if(contains(line.getX1(), line.getY1()) || contains(line.getX2(), line.getY2())) {
			return true;
		}
		for(int i = 0; i < npoints; i++) {
			Line2D.Double side = new Line2D.Double(xpoints[i], ypoints[i], xpoints[(i+1)%npoints], ypoints[(i+1)%npoints]);
			if(side.intersectsLine(line)) {
				return true;
			}
		}
		return false;
	}
	
	public void paint(Graphics g) {
		g.setColor(new Color(100,100,100));
		g.fillPolygon(this);
	}
}
