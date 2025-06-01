package database;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

//klasa odpowiedzialna za polaczenie do enpointa w api oraz przeslaniu informacji i odpowiedzi do logowania
public class LoginService {

	private static String LOGIN_URL = "http://localhost:8080/api/auth/signin";
    public static String login(String username, String password) throws IOException {
    	URL url = new URL(LOGIN_URL);
    	String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);     
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int code = conn.getResponseCode();
        InputStream responseStream;
        if (code == 200) {
        	responseStream = conn.getInputStream();
        }
        else {
        	responseStream = conn.getErrorStream();
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (code == 200) {
            return extractToken(response.toString());
        } else {
        	System.out.println("Kod odpowiedzi : " + code);
            System.out.println("Odpowiedz : " + response.toString());
            throw new IOException("Bład logowania: " + response.toString());
        }
        
    }

    private static String extractToken(String json) {
        String marker = "\"accessToken\":\"";
        int start = json.indexOf(marker);
        if (start == -1) return null;

        start += marker.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;

        return json.substring(start, end);
    }
}
