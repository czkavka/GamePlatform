package database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import session.SessionManager;
/*
 * Klasa odpowiedzialna za wysylanie do backendu zapytania HTTP (CredentialChangeRequest) i zwracania informacji z nowym tokenem 
 * oraz nowym haslem/nazwa uzytkownika
*/
public class ChangeCredentialsService {
	private static final String CHANGE_URL = "http://192.168.1.10:8080/api/auth/credentials";
	
	public static String changeCredentials(String token, String newPassword, String confPassword, String newUsername) throws Exception {
		URL url = new URL(CHANGE_URL);
		JSONObject json = new JSONObject();
	    json.put("newPassword", newPassword);
	    json.put("confirmPassword", confPassword);
	    json.put("newUsername", newUsername);
	    String jsonInput = json.toString();

	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("PUT");
	    conn.setRequestProperty("Content-Type", "application/json; utf-8");
	    conn.setRequestProperty("Accept", "application/json");
	    conn.setRequestProperty("Authorization", "Bearer " + token);
	    conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
        	byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
	        os.write(input, 0, input.length);
	        }

	    int code = conn.getResponseCode();
	    InputStream responseStream;
	    if (code == HttpURLConnection.HTTP_OK)
	    	responseStream = conn.getInputStream();
			else
				responseStream = conn.getErrorStream();

	    StringBuilder response = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
	    	String line;
	        while ((line = br.readLine()) != null) {
	        	response.append(line.trim());
	        	}
	    }
	    if (code == HttpURLConnection.HTTP_OK) {
	        JSONObject jsonResponse = new JSONObject(response.toString());
	      //  System.out.println("auth" + SessionManager.getInstance().getAuthToken());
	        String newToken = jsonResponse.optString("accessToken");
	        newUsername = jsonResponse.optString("username");
	        SessionManager.getInstance().setAuthToken(newToken, newUsername);
	        return "Dane zmienione pomyślnie!";
	        } else {
	        JSONObject errorResponse = new JSONObject(response.toString());
	        String errorMessage = errorResponse.optString("message", "Nieznany błąd");
	        throw new Exception(errorMessage);
	    }
	}
}
