package database;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class LoginService {

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/signin";

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

        if (code == HttpURLConnection.HTTP_OK) {
            responseStream = conn.getInputStream();
        } else {
            responseStream = conn.getErrorStream();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (code == HttpURLConnection.HTTP_OK) {
            return extractToken(response.toString());
        } else {
            String errorMessage = "Błąd logowania (" + code + "): " + response.toString();
            System.err.println(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    private static String extractToken(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.getString("accessToken");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}