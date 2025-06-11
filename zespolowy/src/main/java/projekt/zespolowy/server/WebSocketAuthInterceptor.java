package projekt.zespolowy.server;

import java.util.List;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import projekt.zespolowy.security.jwt.JwtUtils;
import projekt.zespolowy.security.services.UserDetailsServiceImpl;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketAuthInterceptor(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        try {
            List<String> tokens = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().get("token");
            if (tokens == null || tokens.isEmpty() || tokens.get(0).isEmpty()) {
                 System.out.println("Nie można połączyć z socketem, brak tokenu.");
                 return false;
            }
            String token = tokens.get(0);

            if (jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                attributes.put("username", userDetails.getUsername());
                System.out.println("Użytkownik został uwierzytelniony" + username);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas handshake'u " + e.getMessage());
        }

        System.out.println("Nieprawidłowy token");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
