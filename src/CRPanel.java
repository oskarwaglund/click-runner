import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class CRPanel extends JPanel implements MouseListener, KeyListener {

	Hero hero;
	Mesh mesh;
	ArrayList<Wall> walls;
	
	static final int TIMER_DELAY = 20;
	boolean showMesh;
	boolean showPath;
	
	CRPanel() {
		setBackground(new Color(200, 200, 200));
		addMouseListener(this);

		hero = new Hero(200, 400);

		createWalls();
		mesh = new Mesh(walls);

		addKeyListener(this);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				run();
			}
		};
		new Timer(TIMER_DELAY, listener).start();
	}
	
	void createWalls() {
		int WALL_SIZE = 500;
		int WALL_WIDTH = 20;
		
		walls = new ArrayList<>();
		
		//Frame
		//walls.add(new Wall(0, 0, WALL_SIZE, WALL_WIDTH));
		//walls.add(new Wall(0, 0, WALL_WIDTH, WALL_SIZE));
		//walls.add(new Wall(WALL_SIZE-WALL_WIDTH, 0, WALL_WIDTH, WALL_SIZE));
		//walls.add(new Wall(0, WALL_SIZE-WALL_WIDTH, WALL_SIZE, WALL_WIDTH));
		
		//Blocks
		walls.add(new Wall(100, 100, 100, 100));
		walls.add(new Wall(0, 200, 300, WALL_WIDTH));
		walls.add(new Wall(200, 300, 300, WALL_WIDTH));
		walls.add(new Wall(400, 400, WALL_WIDTH, WALL_WIDTH));
		
		//Circle
		walls.add(new Wall(200, 400, 40));
		
		//Sine
		final int sineXStart = 600;
		final int sineYStart = 200;
		int sinePoints = 20;
		
		int[] sineX = new int[sinePoints+2];
		int[] sineY = new int[sinePoints+2];
		
		int[] sineX2 = new int[sinePoints+2];
		int[] sineY2 = new int[sinePoints+2];
		
		final int SINE_INTERVAL = 10;
		for(int i = 0; i < sinePoints; i++) {
			sineX[i] = sineX2[i] = sineXStart + i*SINE_INTERVAL;
			sineY[i] = sineYStart + (int)(20 * Math.sin(4*i*Math.PI/sinePoints));
			sineY2[i] = sineY[i]+20;
		}
		sineX[sinePoints] = sineX2[sinePoints] = sineX[sinePoints-1];
		sineX[sinePoints+1] = sineX2[sinePoints+1] = sineX[0];
		sineY[sinePoints] = sineY[sinePoints+1] = sineY[0] - 100;
		sineY2[sinePoints] = sineY2[sinePoints+1] = sineY2[0] + 100;
		
		walls.add(new Wall(sineX, sineY));
		walls.add(new Wall(sineX2, sineY2));
	}
	
	void run() {
		step();
		paint();
	}

	void step() {
		hero.step();
	}

	void paint() {
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(Wall w: walls) {
			w.paint(g);
		}
		if(showMesh) {
			mesh.paint(g);
		}
		hero.paint(g, showPath);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		requestFocusInWindow();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		hero.setPath(new Point(e.getX(), e.getY()), mesh, walls);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_M:
			showMesh = !showMesh;
			break;
		case KeyEvent.VK_P:
			showPath = !showPath;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
