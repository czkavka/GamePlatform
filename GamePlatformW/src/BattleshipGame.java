import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BattleshipGame extends JFrame {
    private static final int BOARD_SIZE = 5; // Zmniejszona plansza 5x5
    private String playerName;
    private String opponentName;
    private char playerSymbol;
    
    private JButton[][] playerBoard = new JButton[BOARD_SIZE][BOARD_SIZE];
    private JButton[][] opponentBoard = new JButton[BOARD_SIZE][BOARD_SIZE];
    private JLabel statusLabel;
    private JTextArea chatArea;
    private JTextField chatInput;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean myTurn = false;
    private boolean setupPhase = true;
    private int[] shipSizes = {3, 2, 2, 1, 1}; // Mniej statków dla mniejszej planszy
    private int currentShipIndex = 0;
    private List<Point> currentShipCells = new ArrayList<>();
    private boolean isHorizontal = true;

    public BattleshipGame(String playerName, String serverAddress) {
        this.playerName = playerName;
        setTitle("Statki - " + playerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400); // Mniejsze okno
        setLocationRelativeTo(null);
        
        initializeUI();
        connectToServer(serverAddress);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        
        // Top panel with player names
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(new JLabel("Gracz: " + playerName, SwingConstants.CENTER));
        topPanel.add(new JLabel("Przeciwnik: ...", SwingConstants.CENTER));
        add(topPanel, BorderLayout.NORTH);
        
        // Main game boards
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel playerPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 1, 1));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Twoja plansza"));
        initializeBoard(playerPanel, playerBoard, false);
        
        JPanel opponentPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 1, 1));
        opponentPanel.setBorder(BorderFactory.createTitledBorder("Przeciwnik"));
        initializeBoard(opponentPanel, opponentBoard, true);
        
        mainPanel.add(playerPanel);
        mainPanel.add(opponentPanel);
        add(mainPanel, BorderLayout.CENTER);
        
        // Status and controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JButton rotateButton = new JButton("↻");
        rotateButton.setFont(new Font("Arial", Font.BOLD, 12));
        rotateButton.setMargin(new Insets(2, 5, 2, 5));
        rotateButton.setToolTipText("Obróć statek");
        rotateButton.addActionListener(e -> {
            isHorizontal = !isHorizontal;
            clearCurrentShipPreview();
            updateStatus();
        });
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(rotateButton);
        bottomPanel.add(controlsPanel, BorderLayout.WEST);
        
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        updateStatus();
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Chat panel
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea(5, 15);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(120, 0));
        
        chatInput = new JTextField();
        chatInput.addActionListener(e -> sendChatMessage());
        JButton sendButton = new JButton("➤");
        sendButton.setMargin(new Insets(2, 5, 2, 5));
        sendButton.addActionListener(e -> sendChatMessage());
        
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(new JLabel("Czat"), BorderLayout.NORTH);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);
        
        add(chatPanel, BorderLayout.EAST);
    }

    private void initializeBoard(JPanel panel, JButton[][] board, boolean clickable) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new JButton();
                board[i][j].setPreferredSize(new Dimension(30, 30));
                board[i][j].setFont(new Font("Arial", Font.BOLD, 10));
                board[i][j].setFocusPainted(false);
                
                if (!clickable) {
                    board[i][j].setBackground(new Color(173, 216, 230)); // Jasnoniebieski
                    final int x = i;
                    final int y = j;
                    board[i][j].addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) { if (setupPhase) previewShip(x, y); }
                        public void mouseExited(MouseEvent e) { if (setupPhase) clearCurrentShipPreview(); }
                        public void mouseClicked(MouseEvent e) { if (setupPhase) placeShip(x, y); }
                    });
                } else {
                    board[i][j].setBackground(new Color(100, 149, 237)); // Niebieski
                    final int x = i;
                    final int y = j;
                    board[i][j].addActionListener(e -> makeMove(x, y));
                }
                panel.add(board[i][j]);
            }
        }
    }
    
    private void updateStatus() {
        if (setupPhase) {
            statusLabel.setText("<html>Statek: " + shipSizes[currentShipIndex] + " pola | " +
                              (isHorizontal ? "Poziomy" : "Pionowy") + "</html>");
        } else {
            statusLabel.setText(myTurn ? "Twoja tura!" : "Czekaj na przeciwnika");
        }
    }
    
    private void previewShip(int x, int y) {
        clearCurrentShipPreview();
        currentShipCells.clear();
        
        int shipSize = shipSizes[currentShipIndex];
        boolean canPlace = true;
        
        if (isHorizontal) {
            if (y + shipSize > BOARD_SIZE) canPlace = false;
            else {
                for (int i = 0; i < shipSize; i++) {
                    if (playerBoard[x][y+i].getBackground().equals(Color.DARK_GRAY)) {
                        canPlace = false;
                        break;
                    }
                }
            }
            if (canPlace) {
                for (int i = 0; i < shipSize; i++) {
                    playerBoard[x][y+i].setBackground(Color.CYAN);
                    currentShipCells.add(new Point(x, y+i));
                }
            }
        } else {
            if (x + shipSize > BOARD_SIZE) canPlace = false;
            else {
                for (int i = 0; i < shipSize; i++) {
                    if (playerBoard[x+i][y].getBackground().equals(Color.DARK_GRAY)) {
                        canPlace = false;
                        break;
                    }
                }
            }
            if (canPlace) {
                for (int i = 0; i < shipSize; i++) {
                    playerBoard[x+i][y].setBackground(Color.CYAN);
                    currentShipCells.add(new Point(x+i, y));
                }
            }
        }
    }

    
    private void clearCurrentShipPreview() {
        for (Point p : currentShipCells) {
            if (playerBoard[p.x][p.y].getBackground().equals(Color.CYAN)) {
                playerBoard[p.x][p.y].setBackground(new Color(200, 230, 255));
            }
        }
        currentShipCells.clear();
    }
    
    private void placeShip(int x, int y) {
        int shipSize = shipSizes[currentShipIndex];
        boolean canPlace = true;
        
        if (isHorizontal) {
            if (y + shipSize > BOARD_SIZE) canPlace = false;
            else {
                for (int i = 0; i < shipSize; i++) {
                    if (playerBoard[x][y+i].getBackground().equals(Color.DARK_GRAY)) {
                        canPlace = false;
                        break;
                    }
                }
            }
            if (canPlace) {
                for (int i = 0; i < shipSize; i++) {
                    playerBoard[x][y+i].setBackground(Color.DARK_GRAY);
                    playerBoard[x][y+i].setText("■");
                }
                nextShip();
            }
        } else {
            if (x + shipSize > BOARD_SIZE) canPlace = false;
            else {
                for (int i = 0; i < shipSize; i++) {
                    if (playerBoard[x+i][y].getBackground().equals(Color.DARK_GRAY)) {
                        canPlace = false;
                        break;
                    }
                }
            }
            if (canPlace) {
                for (int i = 0; i < shipSize; i++) {
                    playerBoard[x+i][y].setBackground(Color.DARK_GRAY);
                    playerBoard[x+i][y].setText("■");
                }
                nextShip();
            }
        }
        if (!canPlace) {
            JOptionPane.showMessageDialog(this, "Nie można umieścić statku!", "Błąd", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void nextShip() {
        currentShipIndex++;
        if (currentShipIndex < shipSizes.length) {
            updateStatus();
        } else {
            setupPhase = false;
            updateStatus();
            sendBoardToServer();
        }
    }
    
    private void sendBoardToServer() {
        // Tutaj można dodać kod do wysłania ustawień statków do serwera
        // Na przykład: out.println("BOARD:" + zakodowanaPlansza);
    }
    
    private void connectToServer(String serverAddress) {
        new Thread(() -> {
            try {
                socket = new Socket(serverAddress, 5555);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(playerName);

                String response;
                while ((response = in.readLine()) != null) {
                    processMessage(response);
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Błąd połączenia");
                    JOptionPane.showMessageDialog(BattleshipGame.this, 
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
                        updateStatus();
                    }
                    break;
                    
                case "OPPONENT_DISCONNECTED":
                    statusLabel.setText("Przeciwnik rozłączył się");
                    JOptionPane.showMessageDialog(this, "Przeciwnik rozłączył się", 
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                    disableOpponentBoard();
                    break;
                    
                case "MOVE":
                    if (parts.length >= 3) {
                        try {
                            int x = Integer.parseInt(parts[1]);
                            int y = Integer.parseInt(parts[2]);
                            if (playerBoard[x][y].getBackground().equals(Color.DARK_GRAY)) {
                                playerBoard[x][y].setBackground(Color.RED);
                                playerBoard[x][y].setText("X");
                                out.println("RESULT:true:" + x + ":" + y);
                            } else {
                                playerBoard[x][y].setBackground(Color.WHITE);
                                playerBoard[x][y].setText("•");
                                out.println("RESULT:false:" + x + ":" + y);
                            }
                            myTurn = true;
                            updateStatus();
                        } catch (NumberFormatException e) {
                            System.err.println("Błędny format ruchu");
                        }
                    }
                    break;
                    
                case "RESULT":
                    if (parts.length >= 2) {
                        boolean hit = Boolean.parseBoolean(parts[1]);
                        if (hit) {
                            opponentBoard[Integer.parseInt(parts[2])][Integer.parseInt(parts[3])].setBackground(Color.RED);
                            opponentBoard[Integer.parseInt(parts[2])][Integer.parseInt(parts[3])].setText("X");
                        } else {
                            opponentBoard[Integer.parseInt(parts[2])][Integer.parseInt(parts[3])].setBackground(Color.WHITE);
                            opponentBoard[Integer.parseInt(parts[2])][Integer.parseInt(parts[3])].setText("•");
                        }
                        statusLabel.setText(hit ? "Trafiony! Strzelaj ponownie" : "Pudło! Czekaj na przeciwnika");
                        myTurn = hit;
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
        if (!setupPhase && myTurn && opponentBoard[x][y].getBackground() == new Color(180, 180, 255)) {
            out.println("MOVE:" + x + ":" + y);
            opponentBoard[x][y].setBackground(Color.YELLOW);
            opponentBoard[x][y].setText("•");
            opponentBoard[x][y].setEnabled(false);
            myTurn = false;
            statusLabel.setText("Czekaj na wynik...");
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
    
    private void disableOpponentBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                opponentBoard[i][j].setEnabled(false);
            }
        }
    }
    
    
}