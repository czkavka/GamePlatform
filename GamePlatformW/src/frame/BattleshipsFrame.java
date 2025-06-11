package frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class BattleshipsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BattleshipsFrame frame = new BattleshipsFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BattleshipsFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		generateLayout();
		
		
		
	}
	
	private void generateLayout(){
	}
	

}
