package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import frame.TicTacToeFrame;

import java.net.URI;

public class GameClient extends WebSocketClient {

    private TicTacToeFrame gui;

    public GameClient(URI serverUri, TicTacToeFrame gui) {
        super(serverUri);
        this.gui = gui;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Połączono z serwerem.");
        sendJoinLobby("TIC_TAC_TOE"); 

        gui.onConnectedToServer();
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        String type = json.getString("type");

        switch (type) {
            case "GAME_START":
                gui.onGameStart(json.getString("opponent"), json.getString("yourMark"));
                break;
            case "BOARD_UPDATE":
                org.json.JSONArray jsonBoard = json.getJSONArray("board");
                String[][] boardState = new String[3][3];
                for (int i = 0; i < 3; i++) {
                    org.json.JSONArray row = jsonBoard.getJSONArray(i);
                    for (int j = 0; j < 3; j++) {
                        boardState[i][j] = row.getString(j);
                        if (boardState[i][j].equals("null")) {
                            boardState[i][j] = "";
                        }
                    }
                }
                gui.onUpdateState(boardState, json.getBoolean("myTurn")); 
                break;
            case "CHAT_MESSAGE":
                gui.onChatMessage(json.getString("sender"), json.getString("text"));
                break;
            case "GAME_END":
                org.json.JSONArray finalJsonBoard = json.getJSONArray("board");
                String[][] finalBoardState = new String[3][3];
                for (int i = 0; i < 3; i++) {
                    org.json.JSONArray row = finalJsonBoard.getJSONArray(i);
                    for (int j = 0; j < 3; j++) {
                        finalBoardState[i][j] = row.getString(j);
                         if (finalBoardState[i][j].equals("null")) {
                            finalBoardState[i][j] = "";
                        }
                    }
                }
                gui.onGameOver(json.getString("result"), finalBoardState); 
                break;
            case "OPPONENT_DISCONNECTED": 
                gui.onOpponentDisconnected();
                break;
            case "ERROR":
                gui.onWebSocketError(json.getString("message")); 
                break;
            default:
                System.err.println("Nieznany typ wiadomości: " + type + ", treść: " + message);
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Rozłączono z serwerem. Kod: " + code + ", Powód: " + reason + ", Zdalne: " + remote);
        gui.onDisconnected(); 
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Błąd GameClient: " + ex.getMessage());
        ex.printStackTrace();
        gui.onWebSocketError(ex.getMessage());
    }

    public void sendMove(int row, int col) {
        JSONObject move = new JSONObject();
        move.put("type", "MAKE_MOVE");
        move.put("row", row);
        move.put("col", col);
     //   System.out.println("Ruch " + move.toString());
        send(move.toString());
    }

    public void sendChat(String text) { 
        JSONObject chat = new JSONObject();
        chat.put("type", "CHAT_MESSAGE");
        chat.put("text", text);
        send(chat.toString());
    }

    public void sendJoinLobby(String gameType) {
        JSONObject json = new JSONObject();
        json.put("type", "JOIN_LOBBY");
        json.put("gameType", gameType);
        send(json.toString());
    }
}