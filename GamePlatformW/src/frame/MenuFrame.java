package frame;

import java.awt.*;
import javax.swing.*;

import session.SessionManager;
import session.TokenValidation;
import style.GuiStyle;

public class MenuFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainPanel, userPanel, rightPanel;
    private TokenValidation tokenValidation;
    private JLabel homeLabelIcon, settingsLabelIcon, rankingLabelIcon;
    

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
        userPanel.setPreferredSize(new Dimension(100, 0));
        userPanel.setBackground(new Color(36, 47, 65));
        
        homeLabelIcon = GuiStyle.createIcon(homeLabelIcon, "/resources/homeIcon.png", 27, 140, 45, 45);
        settingsLabelIcon = GuiStyle.createIcon(settingsLabelIcon, "/resources/settingsIcon.png", 27, 210, 45, 45);
        rankingLabelIcon = GuiStyle.createIcon(rankingLabelIcon, "/resources/rankingIcon.png", 27, 280, 45, 45);
        
        
        
        userPanel.setLayout(null);    
        userPanel.add(homeLabelIcon);
        userPanel.add(settingsLabelIcon);
        userPanel.add(rankingLabelIcon);
        return userPanel;
    }

    private JPanel createRightPanel() {
    	rightPanel = new JPanel();
        rightPanel.setBackground(new Color(37, 77, 112));
        rightPanel.setLayout(null);
        JLabel chooseLabel = new JLabel("Wybierz grę");
        GuiStyle.applyStyleLabelBasic(chooseLabel, 36, 350,0, 220, 50);
        rightPanel.add(chooseLabel);
        return rightPanel;
    }

}
