package game;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import abstracts.Clock;
import abstracts.KDTree;
import abstracts.Mesh;
import abstracts.Point;
import abstracts.Selection;
import map.Colors;
import map.Wall;
import media.SoundPlayer;
import units.Bomber;
import units.Drone;
import units.Shooter;
import units.Unit;

public class GamePanel extends JPanel implements MouseInputListener, KeyListener {

	Mesh mesh;

	KDTree tree;
	ArrayList<Wall> walls;

	ArrayList<Unit> units;
	ArrayList<Unit> selectedUnits;
	Set<Integer> teams;
	Selection selection;

	boolean showMesh;

	long logicLength;
	long paintLength;

	boolean ctrlPressed;

	public GamePanel() {
		setBackground(Colors.BACKGROUND);
		addMouseListener(this);
		addMouseMotionListener(this);

		units = new ArrayList<>();
		selectedUnits = new ArrayList<>();
		teams = new TreeSet<>();
		for (int i = 0; i < 100; i++) {
			addUnit(new Drone(200 + (i / 10) * 10, 200 + (i % 10) * 10, 1));
		}

		for (int i = 0; i < 100; i++) {
			addUnit(new Shooter(200 + (i / 10) * 10, 500 + (i % 10) * 10, 2));
		}

		for (int i = 0; i < 100; i++) {
			addUnit(Math.random() < 0.5 ? new Drone(200 + (i / 10) * 10, 800 + (i % 10) * 10, 3)
					: new Shooter(200 + (i / 10) * 10, 800 + (i % 10) * 10, 3));
		}

		addUnit(new Bomber(200, 190, 1));

		selection = null;
		walls = new ArrayList<>();
		loadMap("/Mini.txt");
		mesh = new Mesh(walls);

		ctrlPressed = false;

		addKeyListener(this);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				run();
			}
		};
		new Timer(Clock.FRAME_LENGTH, listener).start();
		SoundPlayer.playSound(SoundPlayer.SoundEnum.THEME);
	}

	void run() {
		long start = System.currentTimeMillis();
		stepUnits();
		Clock.tick();
		long mid = System.currentTimeMillis();
		repaint();
		long stop = System.currentTimeMillis();

		logicLength = mid - start;
		paintLength = stop - mid;
	}

	void stepUnits() {
		for (int team : teams) {
			tree = new KDTree(
					units.stream().filter(u -> u.getTeam() != team && !u.isDead()).collect(Collectors.toList()), walls);
			for (Unit u : units.stream().filter(u -> u.getTeam() == team && !u.isDead()).collect(Collectors.toList())) {
				if (u.getAttackTarget() == null) {
					u.setAttackTarget(tree.getClosestEnemy(u));
				}
			}
		}

		KDTree allUnitsTree = new KDTree(units, walls);
		for (Unit unit : units) {
			unit.step(unit.splash() > 0 ? allUnitsTree : null);
		}

		for (int i = units.size() - 1; i >= 0; i--) {
			if (units.get(i).remove()) {
				units.remove(i);
			}
		}
	}

	void addUnit(Unit u) {
		units.add(u);
		teams.add(u.getTeam());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Wall w : walls) {
			w.paint(g);
		}

		if (showMesh) {
			mesh.paint(g);
		}

		for (Unit u : units) {
			u.paint(g, selectedUnits.contains(u));
		}

		int x = 10;
		int y = 10;
		String[] strings = new String[] { "***Controls***", "M:            Toggle mesh", "Left click:   Select units",
				"Right click:  Set path", "", "***Metrics***", "Mesh points:  " + mesh.getPoints().size(),
				"Connections:  " + mesh.getConnections().size(), "Logic length: " + logicLength + " ms",
				"Paint length: " + paintLength + " ms" };

		g.setColor(Colors.TEXT);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		for (String s : strings) {
			g.drawString(s, x, y);
			y += g.getFontMetrics().getHeight();
		}

		if (selection != null) {
			selection.paint(g);
		}
	}

	void loadMap() {
		JFileChooser fc = new JFileChooser();
		int val = fc.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			loadMap(file);
		}
	}

	void loadMap(String s) {
		InputStream in = getClass().getResourceAsStream(s); 
		walls.clear();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	void loadMap(File file) {
		walls.clear();
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
		} catch (IOException e) {
			e.printStackTrace();
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
			if (selection.contains((int) u.getX(), (int) u.getY())) {
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
			selection = new Selection(e.getX(), e.getY());
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
				u.setPath(new Point(e.getX(), e.getY()), mesh, walls, false);
			}
			break;
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			ctrlPressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_M:
			showMesh = !showMesh;
			break;
		case KeyEvent.VK_L:
			loadMap();
			break;
		case KeyEvent.VK_CONTROL:
			ctrlPressed = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selection != null) {
			selection.mouseUpdate(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
