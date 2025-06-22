To make project work add following SQL statements :

CREATE SCHEMA projekt_zespolowy;

INSERT INTO roles (name) VALUES ('ROLE_USER');

INSERT INTO roles (name) VALUES ('ROLE_MODERATOR');

INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO games (name) VALUES ('GAME_TICTACTOE');

INSERT INTO games (name) VALUES ('GAME_SHIPS');

INSERT INTO games (name) VALUES ('GAME_ROCK_PAPER_SCISSORS');



dodaj do menuframe:
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

        // Dodajemy akcję wylogowania
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
