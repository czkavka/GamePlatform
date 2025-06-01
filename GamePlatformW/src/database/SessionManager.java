package database;

//klasa do sprawdzenia czy nasz token juz wygasl
public class SessionManager {

 private static String authToken; 
 private static String username; 

 public static void setAuthToken(String token, String user) {
     authToken = token;
     username = user;
 }

 public static String getAuthToken() {
     return authToken;
 }

 public static boolean isLoggedIn() {
     return authToken != null && !authToken.isEmpty();
 }

 public static String getUsername() {
     return username;
 }

 public static void logout() {
     authToken = null;
     username = null;
 }
}