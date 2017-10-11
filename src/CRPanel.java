import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

public class CRPanel extends JPanel implements MouseInputListener, KeyListener {

	Hero hero;
	Mesh mesh;
	ArrayList<Wall> walls;

	Wall editedWall;

	static final int TIMER_DELAY = 20;
	boolean showMesh;
	boolean showPath;
	
	CRPanel() {
		setBackground(new Color(200, 200, 200));
		addMouseListener(this);
		addMouseMotionListener(this);
		hero = new Hero(200, 400);

		walls = new ArrayList<>();
		mesh = new Mesh(walls);

		addKeyListener(this);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				run();
			}
		};
		new Timer(TIMER_DELAY, listener).start();
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
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		for (Wall w : walls) {
			w.paint(g);
		}
		if (showMesh) {
			mesh.paint(g);
		}
		hero.paint(g, showPath);
		if (editedWall != null) {
			editedWall.paintEdit(g, walls, hero);
		}
		
		int x = 10;
		int y = 10;
		String[] strings = new String[] {
			"***Controls***",
			"M:           Toggle mesh",
			"P:           Toggle path",
			"Left click:  Set path",
			"Right click: Create wall",
			"",
			"***Metrics***",
			"Mesh points: " + mesh.points.size(),
			"Connections: " + mesh.connections.size()
		};
		
		g.setColor(Color.WHITE);
		for(String s: strings) {
			g.drawString(s, x, y);
			y += g.getFontMetrics().getHeight();
		}
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
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			hero.setPath(new Point(e.getX(), e.getY()), mesh, walls);
			break;
		case MouseEvent.BUTTON3:
			if (editedWall == null) {
				editedWall = new Wall();
				editedWall.addPoint(e.getX(), e.getY());
			}
			editedWall.addPoint(e.getX(), e.getY());
			break;
		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_M:
			showMesh = !showMesh;
			break;
		case KeyEvent.VK_P:
			showPath = !showPath;
			break;
		case KeyEvent.VK_ENTER:
			if (editedWall != null && !editedWall.touchesOther(walls, hero)) {
				walls.add(editedWall);
				editedWall = null;
				
				mesh=new Mesh(walls);
				hero.updatePath(mesh, walls);
			}
			mesh = new Mesh(walls);
			break;
		case KeyEvent.VK_ESCAPE:
			editedWall = null;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (editedWall != null) {
			editedWall.moveLast(e.getX(), e.getY());
		}
	}
}
