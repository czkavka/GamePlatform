import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private static final int PORT_BATTLESHIP = 5555;
    private static final int PORT_TICTACTOE = 5556;
    private static final ConcurrentHashMap<String, PlayerHandler> waitingBattleshipPlayers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PlayerHandler> waitingTicTacToePlayers = new ConcurrentHashMap<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(200);

    public static void main(String[] args) {
        new Thread(() -> startServer(PORT_BATTLESHIP, waitingBattleshipPlayers)).start();
        new Thread(() -> startServer(PORT_TICTACTOE, waitingTicTacToePlayers)).start();
        System.out.println("Serwery gier uruchomione (Statki:5555, Kółko i krzyżyk:5556)");
    }

    private static void startServer(int port, ConcurrentHashMap<String, PlayerHandler> waitingPlayers) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler playerThread = new PlayerHandler(clientSocket, waitingPlayers);
                pool.execute(playerThread);
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera na porcie " + port + ": " + e.getMessage());
        }
    }

    static class PlayerHandler implements Runnable {
        private Socket socket;
        private String playerName;
        private PrintWriter out;
        private BufferedReader in;
        private PlayerHandler opponent;
        private volatile boolean running = true;
        private ConcurrentHashMap<String, PlayerHandler> waitingPlayers;

        public PlayerHandler(Socket socket, ConcurrentHashMap<String, PlayerHandler> waitingPlayers) {
            this.socket = socket;
            this.waitingPlayers = waitingPlayers;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String firstMessage = in.readLine();
                if (firstMessage == null) return;
                
                // Sprawdź czy to gra w kółko i krzyżyk (zaczyna się od TTT:)
                if (firstMessage.startsWith("TTT:")) {
                    playerName = firstMessage.substring(4);
                } else {
                    playerName = firstMessage;
                }

                System.out.println(playerName + " dołączył do serwera");
                waitingPlayers.put(playerName, this);
                matchPlayers();

                String inputLine;
                while (running && (inputLine = in.readLine()) != null) {
                    if (opponent != null) {
                        if (inputLine.startsWith("CHAT:")) {
                            opponent.out.println("CHAT:" + playerName + ":" + inputLine.substring(5));
                        } else {
                            opponent.out.println(inputLine);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Błąd w komunikacji z " + playerName + ": " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private void matchPlayers() {
            if (waitingPlayers.size() >= 2) {
                Iterator<Map.Entry<String, PlayerHandler>> iterator = waitingPlayers.entrySet().iterator();
                PlayerHandler player1 = iterator.next().getValue();
                PlayerHandler player2 = iterator.next().getValue();
                
                if (player1 != this && player2 != this) return;
                
                player1.opponent = player2;
                player2.opponent = player1;
                
                player1.out.println("OPPONENT_FOUND:" + player2.playerName + ":X");
                player2.out.println("OPPONENT_FOUND:" + player1.playerName + ":O");
                
                waitingPlayers.remove(player1.playerName);
                waitingPlayers.remove(player2.playerName);
            }
        }

        private void disconnect() {
            running = false;
            try {
                if (opponent != null) {
                    opponent.out.println("OPPONENT_DISCONNECTED");
                    opponent.opponent = null;
                }
                waitingPlayers.remove(playerName);
                socket.close();
                System.out.println(playerName + " opuścił serwer");
            } catch (IOException e) {
                System.err.println("Błąd przy zamykaniu połączenia: " + e.getMessage());
            }
        }
    }
}