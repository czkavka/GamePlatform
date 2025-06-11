package database;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
/*
 * Klasa odpowiedzialna za zwracanie rankingu graczy, po 5 sekundach nastepuje timeout jesli nie polaczono
 */

public class RankingService {
    private static final String BASE_URL = "http://192.168.1.10:8080/api/ranking?eGame=";

    public static JSONArray getRankingForGame(String gameName) throws Exception {
        URL url = new URL(BASE_URL + gameName);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

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
        conn.disconnect();

        if (code >= 200 && code < 300) {
            return new JSONArray(response.toString());
        } else {
            try {
                JSONObject errorObj = new JSONObject(response.toString());
                String message = errorObj.optString("message", "Błąd pobierania rankingu.");
                throw new Exception(message);
            } catch (Exception ex) {
                throw new Exception("Błąd pobierania rankingu.");
            }
        }
    }
}
