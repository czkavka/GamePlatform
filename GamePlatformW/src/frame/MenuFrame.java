package frame;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import database.ChangeCredentialsService;
import session.SessionManager;
import session.TokenValidation;
import style.GuiStyle;
/*
 * Klasa bedaca glowna formatka, gdzie uzytkownik moze sprawdzic ranking, zmienic haslo, zaczac instacje gry
 */

public class MenuFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainPanel, userPanel, rightPanel;
    private JPanel homePanel, settingsPanel, rankingPanel;
    private CardLayout cardLayout;

    private TokenValidation tokenValidation;
    private JLabel logoIcon;
    private JButton homeButton, settingsButton, rankingButton;
    private JButton ticTacToeButton, battleshipsButton, rockPSButton;
    private JButton[] rankingButtons;
    private JPasswordField changePassField, changePassFieldConf;
    private JTextField changeUsernameField;
    private JButton changeSettingsButton;

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
        //zaczyna timer zwiazany z tokenem
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

    //tworzy panel nawigacyjny, pozwalajacy zmieniac layouty 
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
        Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        logoIcon = new JLabel(scaledIcon);
        logoIcon.setBounds(27, 490, 50, 50);

        // akcja wylogowania
       // logoIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MenuFrame.this,
                    "Czy na pewno chcesz się wylogować?", "Wylogowanie",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // zatrzymaj walidację tokena
                    if (tokenValidation != null) {
                        tokenValidation.stopTokenValidation();
                    }
                    // wyczyść sesję
                    SessionManager.getInstance().clearSession();

                    // zamknij obecną ramkę
                    dispose();

                    // uruchom ponownie LoginFrame
                    EventQueue.invokeLater(() -> {
                        try {
                            LoginFrame loginFrame = new LoginFrame(); // ← Upewnij się, że ta klasa istnieje
                            loginFrame.setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null,
                                "Nie udało się uruchomić formatki logowania: " + ex.getMessage(),
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }
        });

        
        userPanel.add(logoIcon);
        userPanel.add(homeButton);
        userPanel.add(settingsButton);
        userPanel.add(rankingButton);

        return userPanel;
    }

    //tworzy odpowiednie layouty w menu i komponenty
    private JPanel createRightPanel() {
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout); 
        
        JPanel[] panels = new JPanel[3];
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new JPanel();
            panels[i].setLayout(null);
            panels[i].setBackground(new Color(30, 60, 89));
        }
        homePanel = panels[0];
        settingsPanel = panels[1];
        rankingPanel = panels[2];
        
        JLabel chooseLabel = new JLabel("Wybierz grę");
        GuiStyle.applyStyleLabelBasic(chooseLabel, 36, 340, 0, 220, 50);
        homePanel.add(chooseLabel);   

        String[] games = { "Kółko - Krzyżyk", "Statki", "Kamień-papier-nożyce" };
        rankingButtons = new JButton[3];
        int[] x = { 50, 340, 640 };

        for (int i = 0; i < games.length; i++) {
            JButton button = GuiStyle.createStyledButton(games[i], 15, x[i], 90, 200, 50);
            rankingButtons[i] = button;
            rankingPanel.add(button);

        }     
        JLabel rankingLabel = new JLabel("Ranking użytkowników");
        rankingLabel = GuiStyle.applyStyleLabelBasic(rankingLabel,36, 250,0, 400, 50);  
        rankingPanel.add(rankingLabel);
        
        JLabel settingsLabel = new JLabel("Ustawienia");
        settingsLabel = GuiStyle.applyStyleLabelBasic(settingsLabel, 36, 350, 0, 220, 50);
        settingsPanel.add(settingsLabel);
        
        rightPanel.add(homePanel, "home");
        rightPanel.add(settingsPanel, "settings");
        rightPanel.add(rankingPanel, "ranking");
        
        ticTacToeButton = GuiStyle.createGradientButton("Kółko-Krzyżyk",25, new Color(25, 45, 85),new Color(55, 90, 160),"/resources/tictactoeIcon.png");
        ticTacToeButton.setBounds(25, 140, 250, 250);
        homePanel.add(ticTacToeButton);
        ticTacToeButton.addActionListener(e -> {
            EventQueue.invokeLater(() -> {
                try {
                    TicTacToeFrame ticTacToeFrame = new TicTacToeFrame();
                    ticTacToeFrame.setVisible(true);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Nie można uruchomić gry Kółko-Krzyżyk: " + ex.getMessage(), "Błąd uruchamiania gry", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        battleshipsButton = GuiStyle.createGradientButton("Statki", 25, new Color(70,30,40),new Color(150, 30, 30), "/resources/shipIcon.png");
        battleshipsButton.setBounds(575, 140, 250,250);
        homePanel.add(battleshipsButton);
        battleshipsButton.addActionListener(e -> {
   
        });      
        rockPSButton = GuiStyle.createGradientButton("Papier kamień nożyce", 18, new Color(30, 70, 40),new Color(30, 150, 30), "/resources/rpsIcon.png");
        rockPSButton.setBounds(300, 220,250,250);
        homePanel.add(rockPSButton);
                
        changeUsernameField = new JTextField();
        changeUsernameField = GuiStyle.applyStyleTextField(changeUsernameField);
        changeUsernameField.setBounds(320,120,250,40);
        settingsPanel.add(changeUsernameField);
        
        changePassField = new JPasswordField();
        changePassField = GuiStyle.applyStyleTextField(changePassField);
        changePassField.setBounds(320,220,250,40);
        settingsPanel.add(changePassField);
          
        changePassFieldConf = new JPasswordField();
        changePassFieldConf = GuiStyle.applyStyleTextField(changePassFieldConf);
        changePassFieldConf.setBounds(320,320,250,40);
        settingsPanel.add(changePassFieldConf);
        
        changeSettingsButton = new JButton("Potwierdź");
        changeSettingsButton = GuiStyle.applyStyleButton(changeSettingsButton, 18);
        changeSettingsButton.setBounds(355, 410, 180, 45);
        settingsPanel.add(changeSettingsButton);
            
        JLabel[] settingsLabels = new JLabel[3];
        String [] text = {"Zmień nazwę użytkownika : ", "Zmień hasło : ", "Potwierdź hasło : "};
        int [] xPos = {30, 167, 130};
        int start = 120;
        
        for (int k = 0; k < settingsLabels.length; k++)
        {
        	settingsLabels[k] = new JLabel(text[k]);
        	settingsLabels[k] = GuiStyle.applyStyleLabelBasic(settingsLabels[k],20,xPos[k],start,300,40);
        	start += 100;
        	settingsPanel.add(settingsLabels[k]);         	
        }
        changeSettingsButton.addActionListener(e -> {
        	String newPassword = new String(changePassField.getPassword());
            String confirmPassword = new String(changePassFieldConf.getPassword());
            String username = changeUsernameField.getText();
            try {
            	String message = ChangeCredentialsService.changeCredentials(SessionManager.getInstance().getAuthToken(),newPassword,confirmPassword,username);
            	tokenValidation.stopTokenValidation();
            	tokenValidation = new TokenValidation(SessionManager.getInstance().getAuthToken(),this);
            	tokenValidation.startTokenValidation();
            	changeUsernameField.setText("");
            	changePassFieldConf.setText("");
            	changePassField.setText("");
            	JOptionPane.showMessageDialog(this, message, "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Błąd!", JOptionPane.ERROR_MESSAGE);
            }
                       
        });
        return rightPanel;
    }
    
    
}
