package frame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import session.SessionManager;
import session.TokenValidation;
import style.GuiStyle;

public class MenuFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainPanel, userPanel, rightPanel;
    private JPanel homePanel, settingsPanel, rankingPanel;
    private CardLayout cardLayout;

    private TokenValidation tokenValidation;
    private JLabel logoIcon;
    private JButton homeButton, settingsButton, rankingButton;
    private JButton ticTacToeButton, battleshipsButton;

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
    	setResizable(false);
        generateLayout();
        String username = SessionManager.getInstance().getUsername();
        tokenValidation = new TokenValidation(SessionManager.getInstance().getAuthToken(), this);
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

        userPanel = createLeftPanel();
        GridBagConstraints gbcLeft = GuiStyle.createGbc(0, 1, 0, 1.0);
        mainPanel.add(userPanel, gbcLeft);

        rightPanel = createRightPanel();
        GridBagConstraints gbcRight = GuiStyle.createGbc(1, 1, 0.9, 1.0);
        mainPanel.add(rightPanel, gbcRight);
    }

    private JPanel createLeftPanel() {
        userPanel = new JPanel();
        userPanel.setPreferredSize(new Dimension(100, 0));
        userPanel.setBackground(new Color(36, 47, 65));
        userPanel.setLayout(null);
        
        homeButton = GuiStyle.createIconButton("/resources/homeIcon.png", 27, 170, 50, 50);
        settingsButton = GuiStyle.createIconButton("/resources/settingsIcon.png", 27, 240, 50, 50);
        rankingButton = GuiStyle.createIconButton("/resources/rankingIcon.png", 27, 310, 50, 50);
        homeButton.addActionListener(e -> cardLayout.show(rightPanel, "home"));
        settingsButton.addActionListener(e -> cardLayout.show(rightPanel, "settings"));
        rankingButton.addActionListener(e -> cardLayout.show(rightPanel, "ranking"));
        
        ImageIcon originalIcon = new ImageIcon(MenuFrame.class.getResource("/resources/loginIcon.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(50,50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        logoIcon = new JLabel(scaledIcon);
        logoIcon.setBounds(27,490, 50,  50);
        
        userPanel.add(logoIcon);
        userPanel.add(homeButton);
        userPanel.add(settingsButton);
        userPanel.add(rankingButton);

        return userPanel;
    }

    private JPanel createRightPanel() {
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);

        homePanel = new JPanel(null);
        homePanel.setBackground(new Color(30, 60, 89));
        JLabel chooseLabel = new JLabel("Wybierz grę");
        GuiStyle.applyStyleLabelBasic(chooseLabel, 36, 340, 0, 220, 50);
        homePanel.add(chooseLabel);

        settingsPanel = new JPanel();
        settingsPanel.setBackground(new Color(70, 100, 130));
        settingsPanel.add(new JLabel("Ustawienia"));

        rankingPanel = new JPanel();
        rankingPanel.setBackground(new Color(80, 130, 180));
        rankingPanel.add(new JLabel("Ranking użytkowników"));

        rightPanel.add(homePanel, "home");
        rightPanel.add(settingsPanel, "settings");
        rightPanel.add(rankingPanel, "ranking");
        
        ticTacToeButton = GuiStyle.createGradientButton("Kółko-Krzyżyk",25, new Color(25, 45, 85),new Color(55, 90, 160),"/resources/tictactoeIcon.png");
        ticTacToeButton.setBounds(25, 140, 250, 250);
        homePanel.add(ticTacToeButton);
        ticTacToeButton.addActionListener(e -> {
        	//TODO wlaczenie gierki, polaczenie z serwerem
        });
        battleshipsButton = GuiStyle.createGradientButton("Statki", 25, new Color(70,30,40),new Color(150, 30, 30), "/resources/shipIcon.png");
        battleshipsButton.setBounds(350, 140, 250,250);
        homePanel.add(battleshipsButton);
        battleshipsButton.addActionListener(e -> {
   
        });
        
        return rightPanel;
    }
}
