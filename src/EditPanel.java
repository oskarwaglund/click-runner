import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

public class EditPanel extends JPanel implements MouseInputListener, KeyListener {

	ArrayList<Wall> walls;
	Wall editedWall;

	GamePanel gamePanel;
	
	static final int TIMER_DELAY = 20;

	public EditPanel(GamePanel gamePanel) {
		setBackground(new Color(200, 200, 200));
		addMouseListener(this);
		addMouseMotionListener(this);

		walls = new ArrayList<>();
		this.gamePanel = gamePanel;
		addKeyListener(this);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				run();
			}
		};
		new Timer(TIMER_DELAY, listener).start();
	}

	void run() {
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		for (Wall w : walls) {
			w.paint(g);
		}
		if (editedWall != null) {
			editedWall.paintEdit(g);
		}

		int x = 10;
		int y = 10;
		String[] strings = new String[] { 
				"***Controls***", 
				"Left click:  Create wall"};

		g.setColor(Color.WHITE);
		for (String s : strings) {
			g.drawString(s, x, y);
			y += g.getFontMetrics().getHeight();
		}
	}
	
	void exportToGame() {
		gamePanel.setWalls(walls);
	}
	
	void exportToFile() {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				FileOutputStream fos = new FileOutputStream(file);
				for(Wall w: walls) {
					fos.write((w.toString() + "\n").getBytes());
				}
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
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

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			if (editedWall == null) {
				editedWall = new Wall();
				editedWall.addPoint(e.getX(), e.getY());
			}
			editedWall.addPoint(e.getX(), e.getY());
			break;
		case MouseEvent.BUTTON3:
			if (editedWall != null && !editedWall.touchesWall(walls)) {
				walls.add(editedWall);
				editedWall = null;
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
		case KeyEvent.VK_E:
			exportToGame();
			break;
		case KeyEvent.VK_S:
			exportToFile();
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
