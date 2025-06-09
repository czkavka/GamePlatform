package database;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class RankingService {
    private static final String BASE_URL = "http://localhost:8080/api/ranking?eGame=";

    public static JSONArray getRankingForGame(String gameName) throws Exception {
        URL url = new URL(BASE_URL + gameName);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();

        BufferedReader inputResponse;
        if (status >= 200 && status < 300) {
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

        conn.disconnect();

        if (status >= 200 && status < 300) {
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
