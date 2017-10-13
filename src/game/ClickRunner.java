package game;

import javax.swing.JFrame;

public class ClickRunner {
	public static void main(String[] args) {
		JFrame gameWindow = new JFrame("ClickRunner: Game");
		GamePanel gameContent = new GamePanel();
		
		
		JFrame editWindow = new JFrame("ClickRunner: Editor");
		EditPanel editContent = new EditPanel(gameContent);
		editWindow.setContentPane(editContent);
		editWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editWindow.setVisible(true);
		editWindow.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		gameWindow.setContentPane(gameContent);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.setVisible(true);
		gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	}
}
