import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Menu extends JFrame {
    private JPanel contentPane;
    private String userLogin;
    private String serverAddress = "localhost";
    
    public Menu(String login) {
        this.userLogin = login;
        initializeUI();
    }
    
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 500); // Zwiększona wysokość dla dodatkowego przycisku
        contentPane = new JPanel();
        contentPane.setBackground(Color.GRAY);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel witajLabel = new JLabel("Witaj, " + userLogin);
        witajLabel.setBounds(106, 17, 331, 45);
        witajLabel.setForeground(Color.YELLOW);
        witajLabel.setFont(new Font("Luckiest Guy", Font.PLAIN, 30));
        contentPane.add(witajLabel);
        
        JButton statkiButton = new JButton("Statki Online");
        statkiButton.addActionListener(e -> startBattleshipGame());
        statkiButton.setBounds(106, 87, 200, 29);
        contentPane.add(statkiButton);
        
        JButton tttButton = new JButton("Kółko i Krzyżyk Online");
        tttButton.addActionListener(e -> startTicTacToeGame());
        tttButton.setBounds(106, 145, 200, 29);
        contentPane.add(tttButton);
        
        JButton settingsButton = new JButton("Ustawienia");
        settingsButton.addActionListener(e -> showSettings());
        settingsButton.setBounds(106, 203, 200, 29);
        contentPane.add(settingsButton);
        
        JButton exitButton = new JButton("Wyjdź");
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setBounds(106, 261, 200, 29);
        contentPane.add(exitButton);
    }
    
    private void startBattleshipGame() {
        try {
            BattleshipGame game = new BattleshipGame(userLogin, serverAddress);
            game.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Błąd przy uruchamianiu gry: " + e.getMessage(), 
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void startTicTacToeGame() {
        try {
            TicTacToeGame game = new TicTacToeGame(userLogin, serverAddress);
            game.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Błąd przy uruchamianiu gry: " + e.getMessage(), 
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSettings() {
        JDialog dialog = new JDialog(this, "Ustawienia", true);
        dialog.setSize(350, 200);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Adres serwera:");
        JTextField serverField = new JTextField(serverAddress);
        JButton saveButton = new JButton("Zapisz");
        
        saveButton.addActionListener(e -> {
            serverAddress = serverField.getText();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Ustawienia zapisane", 
                "Informacja", JOptionPane.INFORMATION_MESSAGE);
        });
        
        panel.add(label);
        panel.add(serverField);
        panel.add(saveButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            String name = JOptionPane.showInputDialog("Podaj swoją nazwę:");
            if (name == null || name.trim().isEmpty()) {
                name = "Gracz";
            }
            
            Menu frame = new Menu(name);
            frame.setVisible(true);
        });
    }
}

