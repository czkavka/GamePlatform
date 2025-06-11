package projekt.zespolowy.session;

import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public interface GameSession {
    void startGame();
    void handleMessage(WebSocketSession session, JSONObject message);
    void notifyOpponentOfDisconnect(WebSocketSession disconnectedPlayer);
    WebSocketSession getPlayer1Session();
    WebSocketSession getPlayer2Session();
}