package projekt.zespolowy.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int PORT_BATTLESHIP = 5555;
    private static final int PORT_TICTACTOE = 5556;
    private static final ConcurrentHashMap<String, PlayerHandler> waitingBattleshipPlayers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PlayerHandler> waitingTicTacToePlayers = new ConcurrentHashMap<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(200);

    public static void main(String[] args) {
        new Thread(() -> startServer(PORT_BATTLESHIP, waitingBattleshipPlayers)).start();
        new Thread(() -> startServer(PORT_TICTACTOE, waitingTicTacToePlayers)).start();
        System.out.println("Serwery gier uruchomione");
    }

    private static void startServer(int port, ConcurrentHashMap<String, PlayerHandler> waitingPlayers) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler playerThread = new PlayerHandler(clientSocket, waitingPlayers);
                pool.execute(playerThread);
            }
        } catch (Exception e) {
            System.err.println("Błąd serwera na porcie " + port + ": " + e.getMessage());
        }
    }
}
