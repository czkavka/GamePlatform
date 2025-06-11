package client;

import org.json.JSONObject;

public interface GameClientListener {
    void onWebSocketConnected();
    void onWebSocketMessageReceived(JSONObject message);
    void onWebSocketDisconnected(String reason);
    void onWebSocketError(String errorMessage);
}