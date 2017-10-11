import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

public class GamePanel extends JPanel implements MouseInputListener, KeyListener {

	final static Color COLOR_SELECTION = new Color(100, 100, 100, 20);

	Mesh mesh;
	ArrayList<Wall> walls;

	ArrayList<Unit> units;
	ArrayList<Unit> selectedUnits;

	Rectangle selection;

	static final int TIMER_DELAY = 20;
	boolean showMesh;
	boolean showPath;

	public GamePanel() {
		setBackground(new Color(200, 200, 200));
		addMouseListener(this);
		addMouseMotionListener(this);

		units = new ArrayList<>();
		selectedUnits = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			units.add(new Drone(200, 200 + 30 * i));
		}

		selection = null;
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
		for (Unit u : units) {
			u.step();
		}
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

		for (Unit u : units) {
			u.paint(g, showPath, selectedUnits.contains(u));
		}

		int x = 10;
		int y = 10;
		String[] strings = new String[] { "***Controls***", "M:           Toggle mesh", "P:           Toggle path",
				"Left click:  Set path", "", "***Metrics***", "Mesh points: " + mesh.points.size(),
				"Connections: " + mesh.connections.size() };

		g.setColor(Color.WHITE);
		for (String s : strings) {
			g.drawString(s, x, y);
			y += g.getFontMetrics().getHeight();
		}

		g.setColor(COLOR_SELECTION);
		if (selection != null) {
			g.fillRect(selection.x, selection.y, selection.width, selection.height);
		}
	}

	void loadMap() {
		JFileChooser fc = new JFileChooser();
		int val = fc.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			ArrayList<Wall> walls = new ArrayList<>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					String[] points = line.split(",");
					Wall w = new Wall();
					for (int i = 0; i < points.length / 2; i++) {
						int x = Integer.parseInt(points[2 * i]);
						int y = Integer.parseInt(points[2 * i + 1]);
						w.addPoint(x, y);
					}
					walls.add(w);
				}
				br.close();
				setWalls(walls);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setWalls(ArrayList<Wall> walls) {
		this.walls = walls;
		mesh = new Mesh(walls);
	}

	void selectUnits() {
		if (selection == null) {
			return;
		}
		selectedUnits.clear();
		for (Unit u : units) {
			if (selection.contains(u.x, u.y)) {
				selectedUnits.add(u);
			}
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
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			selection = new Rectangle(e.getX(), e.getY(), 0, 0);
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			if (selection != null) {
				selectUnits();
				selection = null;
			}
			break;
		case MouseEvent.BUTTON3:
			for (Unit u : selectedUnits) {
				u.setPath(new Point(e.getX(), e.getY()), mesh, walls);
			}
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
		case KeyEvent.VK_L:
			loadMap();
			break;
		case KeyEvent.VK_ESCAPE:

		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selection != null) {
			selection.setFrame(selection.x, selection.y, e.getX() - selection.x, e.getY() - selection.y);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
