package projekt.zespolowy.gameService;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import projekt.zespolowy.session.GameSession;
import projekt.zespolowy.session.TicTacToeSession;

@Service
public class MatchmakingService {

    //przechowuje poczekalnie dla różnych typów gier
    private final Map<String, Queue<PlayerInfo>> waitingLobbies = new ConcurrentHashMap<>();
    
    // przechowuje aktywne sesje dla kazdego gracza
    private final Map<WebSocketSession, GameSession> activeGameSessions = new ConcurrentHashMap<>();
    
    //infromacje o graczu 
    private static class PlayerInfo {
        WebSocketSession session;
        String username;

        PlayerInfo(WebSocketSession session, String username) {
            this.session = session;
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerInfo that = (PlayerInfo) o;
            return session.equals(that.session);
        }

        @Override
        public int hashCode() {
            return session.hashCode();
        }
    }

    public void addPlayerToLobby(WebSocketSession session, String username, String gameType) {
        
        //tworzymy poczekalnie dla danej gry jesli nie istnieje
        waitingLobbies.putIfAbsent(gameType, new ConcurrentLinkedQueue<>());
        Queue<PlayerInfo> lobby = waitingLobbies.get(gameType);
        boolean playerAlreadyInLobby = lobby.stream().anyMatch(p -> p.session.equals(session));
        if (playerAlreadyInLobby) {
            return;
        }
        //dodanie do lobby
        lobby.add(new PlayerInfo(session, username));
        tryToMatchPlayers(gameType);
    }

    private void tryToMatchPlayers(String gameType) {
        Queue<PlayerInfo> lobby = waitingLobbies.get(gameType);
        if (lobby != null && lobby.size() >= 2) {
            //pobieranie graczy z kolejki
            PlayerInfo player1Info = lobby.poll();
            PlayerInfo player2Info = lobby.poll();

            if (player1Info != null && player2Info != null) {
                
                GameSession gameSession;
                if ("TIC_TAC_TOE".equals(gameType)) {
                    gameSession = new TicTacToeSession(player1Info.session, player1Info.username, player2Info.session, player2Info.username);
                } 
                // else if ("BATTLESHIP".equals(gameType)) {
                //    gameSession = new BattleshipSession(player1Info.session, player1Info.username, player2Info.session, player2Info.username);
                // } 
                else {
                    lobby.add(player1Info); 
                    lobby.add(player2Info);
                    return;
                }           
                activeGameSessions.put(player1Info.session, gameSession);
                activeGameSessions.put(player2Info.session, gameSession);
                
                gameSession.startGame();
            } else {
                //dla bezpieczenstwa
                if (player1Info != null) lobby.add(player1Info);
                if (player2Info != null) lobby.add(player2Info);
            }
        }
    }
    
    public void handleGameMessage(WebSocketSession session, JSONObject message) {
        GameSession gameSession = activeGameSessions.get(session);
        if (gameSession != null) {
            gameSession.handleMessage(session, message);
        } else {
            System.err.println("Nie znaleziono sesji! " + session.getId());
        }
    }

    //usuwanie gracza z lobby
    public void removePlayer(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        System.out.println("Usuwanie gracza " + username + session.getId());
        waitingLobbies.values().forEach(lobby -> {
            boolean removed = lobby.removeIf(p -> p.session.equals(session));
            if (removed) {
                System.out.println("Usunięto gracza " + username + session.getId());
            }
        });

        //zakonczenie graczy jesli byla w toku
        GameSession gameSession = activeGameSessions.get(session);
        if (gameSession != null) {
            gameSession.notifyOpponentOfDisconnect(session);
            WebSocketSession player1S = gameSession.getPlayer1Session();
            WebSocketSession player2S = gameSession.getPlayer2Session();

            if (player1S != null) {
                activeGameSessions.remove(player1S);
                System.out.println("Usunieto gracza 1 " + player1S.getId());
            }
            if (player2S != null) {
                activeGameSessions.remove(player2S);
                System.out.println("Usunieto gracza 2 " + player2S.getId());
            }
        } else {
            System.out.println("Gracz nie byl w aktywnej sesji.");
        }
    }
}