package projekt.zespolowy.payload.response;

public class RankingResponse {
    private String username;
    private int score;

    public RankingResponse(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
