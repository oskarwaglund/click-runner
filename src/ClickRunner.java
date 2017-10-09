import javax.swing.JFrame;

public class ClickRunner {
	public static void main(String[] args) {
		JFrame window = new JFrame("ClickRunner");
		CRPanel content = new CRPanel();
		window.setContentPane(content);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	}
}
