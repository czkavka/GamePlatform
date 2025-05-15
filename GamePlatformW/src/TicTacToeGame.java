import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

class TicTacToeGame extends JFrame {
    private static final int BOARD_SIZE = 3;
    private String playerName;
    private String opponentName;
    private char playerSymbol;
    
    private JButton[][] board = new JButton[BOARD_SIZE][BOARD_SIZE];
    private JLabel statusLabel;
    private JTextArea chatArea;
    private JTextField chatInput;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean myTurn = false;

    public TicTacToeGame(String playerName, String serverAddress) {
        this.playerName = playerName;
        setTitle("Kółko i Krzyżyk - " + playerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        initializeUI();
        connectToServer(serverAddress);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Top panel with player names
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(new JLabel("Gracz: " + playerName, SwingConstants.CENTER));
        topPanel.add(new JLabel("Przeciwnik: oczekiwanie...", SwingConstants.CENTER));
        add(topPanel, BorderLayout.NORTH);
        
        // Main game board
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new JButton();
                board[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                board[i][j].setFocusPainted(false);
                
                final int x = i;
                final int y = j;
                board[i][j].addActionListener(e -> makeMove(x, y));
                
                boardPanel.add(board[i][j]);
            }
        }
        
        add(boardPanel, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("Łączenie z serwerem...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Chat panel
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatInput = new JTextField();
        chatInput.addActionListener(e -> sendChatMessage());
        JButton sendButton = new JButton("Wyślij");
        sendButton.addActionListener(e -> sendChatMessage());
        
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(new JLabel("Czat"), BorderLayout.NORTH);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);
        chatPanel.setPreferredSize(new Dimension(200, 0));
        
        add(chatPanel, BorderLayout.EAST);
    }
    
    private void connectToServer(String serverAddress) {
        new Thread(() -> {
            try {
                socket = new Socket(serverAddress, 5556); // Inny port niż statki
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("TTT:" + playerName); // Oznaczenie, że to gra w kółko i krzyżyk

                String response;
                while ((response = in.readLine()) != null) {
                    processMessage(response);
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Błąd połączenia");
                    JOptionPane.showMessageDialog(TicTacToeGame.this, 
                        "Błąd połączenia: " + e.getMessage(), 
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    private void processMessage(String message) {
        System.out.println("Otrzymano: " + message);
        String[] parts = message.split(":", 3);
        
        SwingUtilities.invokeLater(() -> {
            switch (parts[0]) {
                case "OPPONENT_FOUND":
                    if (parts.length >= 3) {
                        opponentName = parts[1];
                        playerSymbol = parts[2].charAt(0);
                        myTurn = (playerSymbol == 'X');
                        updateTopPanel();
                        statusLabel.setText(myTurn ? "Twoja tura!" : "Czekaj na ruch przeciwnika");
                    }
                    break;
                    
                case "OPPONENT_DISCONNECTED":
                    statusLabel.setText("Przeciwnik rozłączył się");
                    JOptionPane.showMessageDialog(this, "Przeciwnik rozłączył się", 
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                    disableBoard();
                    break;
                    
                case "MOVE":
                    if (parts.length >= 3) {
                        try {
                            int x = Integer.parseInt(parts[1]);
                            int y = Integer.parseInt(parts[2]);
                            char symbol = parts.length >= 4 ? parts[3].charAt(0) : (playerSymbol == 'X' ? 'O' : 'X');
                            board[x][y].setText(String.valueOf(symbol));
                            board[x][y].setEnabled(false);
                            myTurn = true;
                            statusLabel.setText("Twoja tura!");
                        } catch (NumberFormatException e) {
                            System.err.println("Błędny format ruchu");
                        }
                    }
                    break;
                    
                case "RESULT":
                    if (parts.length >= 2) {
                        String result = parts[1];
                        if (result.equals("WIN")) {
                            statusLabel.setText("Wygrałeś!");
                            JOptionPane.showMessageDialog(this, "Gratulacje, wygrałeś!", "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
                        } else if (result.equals("LOSE")) {
                            statusLabel.setText("Przegrałeś!");
                            JOptionPane.showMessageDialog(this, "Niestety, przegrałeś.", "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
                        } else if (result.equals("DRAW")) {
                            statusLabel.setText("Remis!");
                            JOptionPane.showMessageDialog(this, "Remis!", "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
                        }
                        disableBoard();
                    }
                    break;
                    
                case "CHAT":
                    if (parts.length == 3) {
                        String sender = parts[1];
                        String msg = parts[2];
                        chatArea.append(sender + ": " + msg + "\n");
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    }
                    break;
            }
        });
    }
    
    private void updateTopPanel() {
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
        topPanel.removeAll();
        topPanel.add(new JLabel("Gracz: " + playerName + " (" + playerSymbol + ")", SwingConstants.CENTER));
        topPanel.add(new JLabel("Przeciwnik: " + opponentName + " (" + (playerSymbol == 'X' ? 'O' : 'X') + ")", SwingConstants.CENTER));
        topPanel.revalidate();
        topPanel.repaint();
    }
    
    private void makeMove(int x, int y) {
        if (myTurn && board[x][y].getText().isEmpty()) {
            out.println("MOVE:" + x + ":" + y);
            board[x][y].setText(String.valueOf(playerSymbol));
            board[x][y].setEnabled(false);
            myTurn = false;
            statusLabel.setText("Czekaj na ruch przeciwnika...");
        }
    }
    
    private void sendChatMessage() {
        String message = chatInput.getText();
        if (!message.isEmpty()) {
            out.println("CHAT:" + message);
            chatArea.append("Ty: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            chatInput.setText("");
        }
    }
    
    private void disableBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j].setEnabled(false);
            }
        }
    }
}