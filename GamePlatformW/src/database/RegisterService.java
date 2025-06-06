package database;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class RegisterService {
	private static String REGISTER_URL = "http://localhost:8080/api/auth/signup";
	
	public static String register(String username, String email, String password) throws Exception {
	    URL url = new URL(REGISTER_URL);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json; utf-8");
	    conn.setRequestProperty("Accept", "application/json");
	    conn.setDoOutput(true);

	    JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("email", email);
        json.put("password", password);
        String jsonInputString = json.toString();

	    try (OutputStream os = conn.getOutputStream()) {
	        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
	        os.write(input, 0, input.length);
	    }

	    int code = conn.getResponseCode();

	    BufferedReader inputResponse;
	    if (code >= 200 && code < 300) {
	        inputResponse = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
	    } else {
	        inputResponse = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
	    }

	    StringBuilder response = new StringBuilder();
	    String line;
	    while ((line = inputResponse.readLine()) != null) {
	        response.append(line.trim());
	    }
	    inputResponse.close();

	    String message = extractMessage(response.toString());

	    if (code == 200) {
	        return message != null ? message : "Rejestracja zakończona sukcesem.";
	    }
	    else if (code == 400) {
	    	return message;
	    }
	    else {
	        throw new Exception(message != null ? message : "Błąd rejestracji.");
	    }
	}

	private static String extractMessage(String json) {
	    try {
	        JSONObject obj = new JSONObject(json);
	        return obj.getString("message");
	    } catch (Exception e) {
	        return null;
	    }
	}
}