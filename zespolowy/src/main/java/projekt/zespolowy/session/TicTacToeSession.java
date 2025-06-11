package projekt.zespolowy.session;

import java.io.IOException;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class TicTacToeSession implements GameSession {

    private final WebSocketSession player1Session;
    private final String playerOneUsername;
    private final WebSocketSession player2Session;
    private final String playerTwoUsername;
    private char[][] board;
    private char currentPlayerMark;

    public TicTacToeSession(WebSocketSession player1Session, String player1Username,
                            WebSocketSession player2Session, String player2Username) {
        this.player1Session = player1Session;
        this.playerOneUsername = player1Username;
        this.player2Session = player2Session;
        this.playerTwoUsername = player2Username;
        this.board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        Random rand = new Random();
        boolean symbol = rand.nextBoolean();
        if (symbol == true){
            this.currentPlayerMark = 'X';
        }
        else {
            this.currentPlayerMark = 'O';
        }
        
    }
    @Override
    public void startGame() {
        try {
            JSONObject startGameMessageOne = new JSONObject();
            startGameMessageOne.put("type", "GAME_START");
            startGameMessageOne.put("opponent", playerTwoUsername);
            startGameMessageOne.put("yourMark", "X");
            player1Session.sendMessage(new TextMessage(startGameMessageOne.toString()));

            JSONObject startGameMessageTwo = new JSONObject();
            startGameMessageTwo.put("type", "GAME_START");
            startGameMessageTwo.put("opponent", playerOneUsername);
            startGameMessageTwo.put("yourMark", "O");
            player2Session.sendMessage(new TextMessage(startGameMessageTwo.toString()));
            sendBoardState();
        } catch (IOException e) {
            System.err.println("Blad zainicjowania gry " + e.getMessage());
        }
    }


    @Override
    public void handleMessage(WebSocketSession session, JSONObject message) {
        String messageType = message.getString("type");

        switch (messageType) {
            case "MAKE_MOVE":
                handleMove(session, message);
                break;
            case "CHAT_MESSAGE":
                handleChatMessage(session, message);
                break;
        }
    }

    private void handleMove(WebSocketSession session, JSONObject message) {
        int row = message.getInt("row");
        int col = message.getInt("col");

        char mark = (session.equals(player1Session)) ? 'X' : 'O';
        board[row][col] = mark;

        if (checkWin(mark)) {
            sendGameEndMessage(mark + " wygrywa!");
        } else if (checkDraw()) {
            sendGameEndMessage("Remis!");
        } else {
            currentPlayerMark = (currentPlayerMark == 'X') ? 'O' : 'X';
            sendBoardState();
        }
    }
    //sprawdzenie kto wygral
    private boolean checkWin(char mark) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == mark && board[i][1] == mark && board[i][2] == mark) ||
                (board[0][i] == mark && board[1][i] == mark && board[2][i] == mark)) {
                return true;
            }
        }
        if ((board[0][0] == mark && board[1][1] == mark && board[2][2] == mark) ||
            (board[0][2] == mark && board[1][1] == mark && board[2][0] == mark)) {
            return true;
        }
        return false;
    }
    //sprawdzenie remisu
    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    //wysyla obecny stan planszy
    private void sendBoardState() {
        // Wiadomosc dla gracza 1
        JSONObject boardStateMessageOne = new JSONObject();
        boardStateMessageOne.put("type", "BOARD_UPDATE");
        boardStateMessageOne.put("board", boardToJsonArray());
        boardStateMessageOne.put("turn", getCurrentTurnUsername());
        //czy to tura gracza 1 
        boardStateMessageOne.put("myTurn", (currentPlayerMark == 'X')); 

        JSONObject boardStateMessageTwo = new JSONObject();
        boardStateMessageTwo.put("type", "BOARD_UPDATE");
        boardStateMessageTwo.put("board", boardToJsonArray());
        boardStateMessageTwo.put("turn", getCurrentTurnUsername());
        boardStateMessageTwo.put("myTurn", (currentPlayerMark == 'O')); 

        try {
            player1Session.sendMessage(new TextMessage(boardStateMessageOne.toString()));
            player2Session.sendMessage(new TextMessage(boardStateMessageTwo.toString()));
        } catch (IOException e) {
            System.err.println("Error sending board state: " + e.getMessage());
        }
    }
    //zamienia plansze z charu [] na Jsona na taka [[..],[..],[...]] - row
    private JSONArray boardToJsonArray() {
        JSONArray jsonBoard = new JSONArray();
        for (int i = 0; i < 3; i++) {
            JSONArray row = new JSONArray();
            for (int j = 0; j < 3; j++) {
                row.put(String.valueOf(board[i][j]).trim());
            }
            jsonBoard.put(row);
        }
        return jsonBoard;
    }

    //wyslij wiadomosc o koncu gry do klienta
    private void sendGameEndMessage(String result) {
        JSONObject gameEndMessage = new JSONObject();
        gameEndMessage.put("type", "GAME_END");
        gameEndMessage.put("result", result);
        gameEndMessage.put("board", boardToJsonArray());

        try {
            player1Session.sendMessage(new TextMessage(gameEndMessage.toString()));
            player2Session.sendMessage(new TextMessage(gameEndMessage.toString()));
        } catch (IOException e) {
            System.err.println("Błąd wysyłania wiadomości" + e.getMessage());
        }
    }

    private String getCurrentTurnUsername() {
        return (currentPlayerMark == 'X') ? playerOneUsername : playerTwoUsername;
    }

    //
    private void handleChatMessage(WebSocketSession senderSession, JSONObject message) {
        String chatMessage = message.getString("text");
        String senderUsername = (String) senderSession.getAttributes().get("username");

        JSONObject chatJson = new JSONObject();
        chatJson.put("type", "CHAT_MESSAGE");
        chatJson.put("sender", senderUsername);
        chatJson.put("text", chatMessage);

        try {
            player1Session.sendMessage(new TextMessage(chatJson.toString()));
            player2Session.sendMessage(new TextMessage(chatJson.toString()));
        } catch (IOException e) {
            System.err.println("Błąd w wysyłaniu wiadomości: " + e.getMessage());
        }
    }

    //wysyla wiadomosci o rozlaczeniu gracza
    @Override
    public void notifyOpponentOfDisconnect(WebSocketSession disconnectedPlayer) {
        WebSocketSession opponentSession = (disconnectedPlayer.equals(player1Session)) ? player2Session : player1Session;
        String disconnectedUsername = (String) disconnectedPlayer.getAttributes().get("username");

        if (opponentSession != null && opponentSession.isOpen()) {
            try {
                JSONObject disconnectMessage = new JSONObject();
                disconnectMessage.put("type", "OPPONENT_DISCONNECTED");
                disconnectMessage.put("message", disconnectedUsername + " has disconnected. Game ended.");
                opponentSession.sendMessage(new TextMessage(disconnectMessage.toString()));
            } catch (IOException e) {
                System.err.println("Błąd w wysłaniu wiadomości o rozłączeniu : " + e.getMessage());
            }
        }
    }

    @Override
    public WebSocketSession getPlayer1Session() {
        return player1Session;
    }

    @Override
    public WebSocketSession getPlayer2Session() {
        return player2Session;
    }

}