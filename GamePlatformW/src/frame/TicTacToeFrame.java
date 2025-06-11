package frame;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import client.GameClient;
import session.SessionManager;
import style.GuiStyle;

/*
 * Formatka z gra kolko-krzyzyk
 */
public class TicTacToeFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton[][] board = new JButton[3][3];
    private JPanel mainPanel, topPanel, boardPanel, chatPanel, chatInputPanel;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel statusLabel, enemyLabel, userLabel;
    private JButton sendButton;

    private GameClient client;
    private String playerSymbol;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                if (!SessionManager.getInstance().isLoggedIn()) {
                    JOptionPane.showMessageDialog(null, "Brak sesji użytkownika", "Błąd Autoryzacji", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                TicTacToeFrame frame = new TicTacToeFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }  
    @Override
    public void dispose() {
        if (client != null && client.isOpen()) {
            client.close();        
        }
        super.dispose();
    }
    

    public TicTacToeFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(36, 47, 65));
        setContentPane(mainPanel);

        //funkcja do generowania layoutu
        generateGameLayout();

        //Inicjalizacja websocketa
        try {
            String token = SessionManager.getInstance().getAuthToken();
            
            String wsUrl = "ws://localhost:8080/ws/game?token=" + token;
            client = new GameClient(new URI(wsUrl), this);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
  
        }
        //dodanie listenerow
        addBoardActionListeners();
        addChatSendListener();
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
                board[i][j].setEnabled(false);
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
        chatArea.setForeground(new Color(255, 255, 255));
        chatArea.setCaretColor(new Color(255, 255, 255));
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
        chatInput.setEnabled(false);

        sendButton = new JButton("Wyślij");
        sendButton = GuiStyle.applyStyleButton(this.sendButton, 11);
        sendButton.setPreferredSize(new Dimension(70, 30));
        sendButton.setEnabled(false); 

        chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.setBackground(new Color(36, 47, 65));
        chatInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(this.sendButton, BorderLayout.EAST);
        chatPanel.add(scrollPanel, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        mainPanel.add(chatPanel, BorderLayout.EAST);
    }

    private void addBoardActionListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;
                board[i][j].addActionListener(e -> {
                    if (board[row][col].isEnabled() && board[row][col].getText().isEmpty()) {
                        client.sendMove(row, col);
                        disableBoardButtons();
                    }
                });
            }
        }
    }

    private void addChatSendListener() {
        sendButton.addActionListener(e -> {
            String message = chatInput.getText();
            if (!message.trim().isEmpty()) {
                client.sendChat(message);
                chatInput.setText("");
            }
        });
    }
    //metoda - polaczenie z serverem
    public void onConnectedToServer() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Oczekiwanie w poczekalni...");
            chatInput.setEnabled(true);
            this.sendButton.setEnabled(true);
        });
    }
    //metoda wywolywana po rozpaczeniu gry - czyli polaczeniu dwoch graczy
    public void onGameStart(String opponentName, String yourMark) {
        SwingUtilities.invokeLater(() -> {
            this.playerSymbol = yourMark;
            setTitle("Kółko i Krzyżyk - Grasz jako " + yourMark);
            enemyLabel.setText("Przeciwnik: " + opponentName);
        });
    }
    //metoda do wykonania ruchu
    public void onUpdateState(String[][] boardState, boolean myTurn) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    String value = boardState[i][j];
                    board[i][j].setText(value);
                    board[i][j].setEnabled(myTurn && value.isEmpty());
                }
            }
            statusLabel.setText(myTurn ? "Twoja tura! Wykonaj ruch." : "Tura przeciwnika...");
        });
    }
    //metoda po wyslaniu wiadomosci
    public void onChatMessage(String sender, String text) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(sender + ": " + text + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    //gdy gra sie konczy
    public void onGameOver(String result, String[][] finalBoardState) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    String value = finalBoardState[i][j];
                    board[i][j].setText(value);
                     if (value.equals("X")) {
                        board[i][j].setForeground(new Color(255, 99, 71));
                    } else if (value.equals("O")) {
                        board[i][j].setForeground(new Color(100, 149, 237));
                    } else {
                         board[i][j].setForeground(Color.WHITE);
                    }
                    board[i][j].setEnabled(false);
                }
            }
            String messageResult = "";
            switch (result) {
                case "WIN":
                    statusLabel.setText("Wygrałeś! Gratulacje!");
                    messageResult = "Wygrałeś! Gratulacje!";
                    break;
                case "LOSE":
                    statusLabel.setText("Przegrałeś. Spróbuj ponownie!");
                    messageResult = "Przegrałeś. Spróbuj ponownie!";
                    break;
                case "DRAW":
                    statusLabel.setText("Remis!");
                    messageResult = "Remis!";
                    break;
                default:
                    statusLabel.setText("Gra zakończona: " + result);
                    messageResult = "Gra zakończona: " + result;
                    break;
            }
            JOptionPane.showMessageDialog(this, messageResult, "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    //wywolywane gdy przeciwnik sie rozlaczy 
    public void onOpponentDisconnected() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Przeciwnik się rozłączył. Koniec gry.");
            disableBoardButtons();
            chatInput.setEnabled(false); 
            this.sendButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Przeciwnik rozłączył się. Gra zakończona.", "Przeciwnik rozłączony", JOptionPane.WARNING_MESSAGE);
        });
    }
    public void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Rozłączono z serwerem.");
            disableBoardButtons();
            chatInput.setEnabled(false);
            this.sendButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Połączenie z serwerem zostało utracone.", "Rozłączenie", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void onWebSocketError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Błąd: " + errorMessage);
            JOptionPane.showMessageDialog(this, "Wystąpił błąd WebSocketa: " + errorMessage, "Błąd", JOptionPane.ERROR_MESSAGE);
        });
    }
    private void disableBoardButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setEnabled(false);
            }
        }
    }

}