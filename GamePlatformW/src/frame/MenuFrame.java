package frame;

import java.awt.*;
import javax.swing.*;

import session.SessionManager;
import session.TokenValidation;
import style.GuiStyle;

public class MenuFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainPanel, userPanel, rightPanel, topPanel;
    private TokenValidation tokenValidation;
    

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MenuFrame frame = new MenuFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MenuFrame() {
        generateLayout();
        String username = SessionManager.getInstance().getUsername();
        tokenValidation = new TokenValidation(SessionManager.getInstance().getAuthToken(),this);
        tokenValidation.startTokenValidation();
        
    }
    @Override
    public void dispose() {
        if (tokenValidation != null) {
            tokenValidation.stopTokenValidation();
        }
        super.dispose();      
    }

    private void generateLayout() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        mainPanel = new JPanel(new GridBagLayout());
        setContentPane(mainPanel);
        
        //pasek 
        topPanel = createTopPanel();
        GridBagConstraints gbcTop = GuiStyle.createGbc(0, 0, 1.0, 0.0);
        gbcTop.gridwidth = 2;
        mainPanel.add(topPanel, gbcTop);
        //lewa strona - informacje o uzytkowniku, 
        userPanel = createLeftPanel();
        GridBagConstraints gbcLeft = GuiStyle.createGbc(0, 1, 0, 1.0);
        mainPanel.add(userPanel, gbcLeft);
        
        //content z grami
        rightPanel = createRightPanel();
        GridBagConstraints gbcRight = GuiStyle.createGbc(1, 1, 0.9, 1.0);
        mainPanel.add(rightPanel, gbcRight);
    }

    private JPanel createLeftPanel() {
    	userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());
        userPanel.setBackground(new Color(240, 240, 240));
        userPanel.setPreferredSize(new Dimension(100, 0));

        return userPanel;
    }

    private JPanel createRightPanel() {
    	rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);
        JLabel chooseLabel = new JLabel("Wybierz grę");
        GuiStyle.applyStyleLabelBasic(chooseLabel, 32, 350, 0, 200, 40);
        chooseLabel.setForeground(new Color(0, 0, 0));
        rightPanel.add(chooseLabel);
        return rightPanel;
    }

    private JPanel createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 50));
        topPanel.setBackground(new Color(200, 200, 200));
        return topPanel;
    }
}
