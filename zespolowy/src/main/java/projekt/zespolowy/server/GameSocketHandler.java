package projekt.zespolowy.server;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import projekt.zespolowy.gameService.MatchmakingService;
/*
 * Klasa pozwalajaca obsługiwać tekstowe wiadomości WebSocket
 */
@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private final MatchmakingService matchmakingService;

    public GameSocketHandler(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        System.out.println("Połączono '" + username + "'. Session ID: " + session.getId());
    }

    //obsluguje przychodzące wiadomości od gracza, np. czy gracz dolaczyl do rozgrywki 
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String username = (String) session.getAttributes().get("username");
        JSONObject jsonMsg = new JSONObject(message.getPayload());
        String type = jsonMsg.getString("type");

        if ("JOIN_LOBBY".equals(type)) {
            String gameType = jsonMsg.getString("gameType");
            matchmakingService.addPlayerToLobby(session, username, gameType);
        } else {
            matchmakingService.handleGameMessage(session, jsonMsg);
        }
    }
    //metoda wywolana zostanie kiedy gracz rozlaczy sie 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("username");
        if(username != null) {
            System.out.println("Uzytkownik rozłączył się " + username + "Powód " + status.getReason());
            matchmakingService.removePlayer(session);
        }
    }
}