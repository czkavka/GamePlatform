package projekt.zespolowy.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerHandler implements Runnable {
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
            } catch (Exception e) {
                System.err.println("Błąd w komunikacji z " + playerName + ": " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private void matchPlayers() {
            if (waitingPlayers.size() >= 2) {
                Iterator<Map.Entry<String, PlayerHandler>> iterator = waitingPlayers.entrySet().iterator();
                PlayerHandler playerOne = iterator.next().getValue();
                PlayerHandler playerTwo = iterator.next().getValue();           
                
                Random random = new Random();
                boolean randomSymbol = random.nextBoolean();
                
                if (playerOne != this && playerTwo != this) return;
                
                playerOne.opponent = playerTwo;
                playerTwo.opponent = playerOne;

                if (randomSymbol) {
                    playerOne.out.println("OPPONENT_FOUND:" + playerTwo.playerName + ":X");
                    playerTwo.out.println("OPPONENT_FOUND:" + playerOne.playerName + ":O");
                } else {
                    playerOne.out.println("OPPONENT_FOUND:" + playerTwo.playerName + ":O");
                    playerTwo.out.println("OPPONENT_FOUND:" + playerOne.playerName + ":X");
                }
                
                waitingPlayers.remove(playerOne.playerName);
                waitingPlayers.remove(playerTwo.playerName);
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
            } catch (Exception e) {
                System.err.println("Błąd przy zamykaniu połączenia: " + e.getMessage());
            }
        }
    }
