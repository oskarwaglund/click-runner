package abstracts;

import java.awt.Graphics;

import map.Colors;

public class Selection {
	private int rootX, rootY;
	private int mouseX, mouseY;
	
	public Selection(int rootX, int rootY) {
		this.rootX = rootX;
		this.rootY = rootY;
		this.mouseX = rootX;
		this.mouseY = rootY;
	}
	
	public void mouseUpdate(int x, int y) {
		mouseX = x;
		mouseY = y;
	}
	
	public boolean contains(int x, int y) {
		return (rootX - x)*(mouseX - x) <= 0 && (rootY - y)*(mouseY - y) <= 0;
	}
	
	public void paint(Graphics g) {
		g.setColor(Colors.SELECTION);
		g.fillRect(Math.min(rootX, mouseX), Math.min(rootY, mouseY), Math.abs(mouseX - rootX), Math.abs(mouseY - rootY));
	}
	

}
