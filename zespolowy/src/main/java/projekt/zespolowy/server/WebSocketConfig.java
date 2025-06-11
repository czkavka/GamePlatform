package projekt.zespolowy.server;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameSocketHandler gameSocketHandler;
    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(GameSocketHandler gameSocketHandler, WebSocketAuthInterceptor authInterceptor) {
        this.gameSocketHandler = gameSocketHandler;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameSocketHandler, "/ws/game")
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }
}