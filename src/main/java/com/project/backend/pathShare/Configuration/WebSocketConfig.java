package com.project.backend.pathShare.Configuration;

import com.project.backend.pathShare.Service.RideNotificationHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new RideNotificationHandler(), "/driver-notifications").setAllowedOrigins("*");
    }
}
