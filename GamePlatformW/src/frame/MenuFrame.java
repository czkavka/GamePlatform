package frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MenuFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuFrame frame = new MenuFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public MenuFrame() {
	//generuje rozmieszczenie layoutow  
		generateLayout();
	  
	}
	
	private void generateLayout()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 550);
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		
	
		
		
	}
	

}
