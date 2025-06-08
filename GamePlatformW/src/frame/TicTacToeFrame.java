package frame;

import java.awt.*;
import javax.swing.*;

import session.SessionManager;
import style.GuiStyle;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class TicTacToeFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton[][] board = new JButton[3][3];
    private JPanel mainPanel, topPanel, boardPanel, chatPanel, chatInputPanel;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel statusLabel, enemyLabel, userLabel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                TicTacToeFrame frame = new TicTacToeFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public TicTacToeFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(36, 47, 65));
        setContentPane(mainPanel);

        generateGameLayout();
    }

    public void generateGameLayout() {
        String username = SessionManager.getInstance().getUsername();
        topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 175));
        topPanel.setBackground(new Color(36, 47, 65));

        userLabel = GuiStyle.applyStyleLabelBasic(new JLabel("Gracz: " + username), 16, 0, 0, 0, 0);
        userLabel.setHorizontalAlignment(SwingConstants.LEFT);
        topPanel.add(userLabel);

        enemyLabel = GuiStyle.applyStyleLabelBasic(new JLabel("Przeciwnik: oczekiwanie..."), 16, 0, 0, 0, 0);
        enemyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(enemyLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setBackground(new Color(36, 47, 65));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = new JButton();
                board[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                board[i][j].setFocusPainted(false);
                board[i][j].setBackground(new Color(50, 60, 75));
                board[i][j].setForeground(Color.WHITE);
                board[i][j].setBorder(BorderFactory.createLineBorder(new Color(70, 80, 100)));
                boardPanel.add(board[i][j]);
            }
        }

        mainPanel.add(boardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Oczekiwanie na połączenie...");
        statusLabel = GuiStyle.applyStyleLabelBasic(statusLabel, 16, 0, 0, 0, 0);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(250, 0));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        chatPanel.setBackground(new Color(36, 47, 65));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        chatArea.setBackground(new Color(50, 60, 75));
        chatArea.setForeground(new Color(255,255,255));
        chatArea.setCaretColor(new Color(255,255,255));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPanel = new JScrollPane(chatArea);
        scrollPanel.setPreferredSize(new Dimension(250, 300));
        scrollPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 80, 100)));
        scrollPanel.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 110, 130);
            }
        });

        chatInput = new JTextField();
        chatInput.setBackground(new Color(60, 70, 85));
        chatInput.setForeground(new Color(255, 255, 255));
        chatInput.setCaretColor(new Color(255, 255, 255));
        chatInput.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton sendButton = new JButton("Wyślij");
        sendButton = GuiStyle.applyStyleButton(sendButton, 11);
        sendButton.setPreferredSize(new Dimension(70, 30));
        
        
        chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.setBackground(new Color(36, 47, 65));
        chatInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(scrollPanel, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        mainPanel.add(chatPanel, BorderLayout.EAST);
    }
}
