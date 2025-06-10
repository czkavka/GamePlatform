package session;
import javax.swing.*;
import java.net.HttpURLConnection;
import java.net.URL;

//sprawdzanie waznosci tokenu, co 10 sekund
public class TokenValidation {

    private final String token;
    private final JFrame frame;
    private Timer tokenValidationTimer;
    private static final String TOKEN_URL = "http://localhost:8080/api/auth/validate";

    public TokenValidation(String token, JFrame frame) {
        this.token = token;
        this.frame = frame;
    }

    public void startTokenValidation() {
        tokenValidationTimer = new Timer(10000, e -> {
            if (!isTokenValid()) {
                tokenValidationTimer.stop();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Sesja wygasła, wylogowano");
                    SessionManager.getInstance().clearSession();
                    frame.dispose();
                });
            }
        });
        tokenValidationTimer.start();
    }

    public void stopTokenValidation() {
        if (tokenValidationTimer != null) {
            tokenValidationTimer.stop();
        }
    }
    public boolean isTokenValid() {
        try {
            URL url = new URL(TOKEN_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.connect();

            int responseCode = con.getResponseCode();
            con.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    
}
