import javax.swing.JFrame;

public class ClickRunner {
	public static void main(String[] args) {
		JFrame window = new JFrame("ClickRunner");
		CRPanel content = new CRPanel();
		window.setContentPane(content);
		window.setSize(700, 700);
		window.setLocation(100, 100);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
