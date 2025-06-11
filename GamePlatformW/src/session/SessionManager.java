package session;

/*
 * Singleton przechowujacy sesje uzytkownika
 */
public class SessionManager {
    private static SessionManager instance;
    private String authToken;
    private String username;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void setAuthToken(String token, String username) {
        this.authToken = token;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setUsername(String username) {
    	this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void clearSession() {
        this.authToken = null;
        this.username = null;
    }

    public boolean isLoggedIn() {
        return authToken != null;
    }
}
