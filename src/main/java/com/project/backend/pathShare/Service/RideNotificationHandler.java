package com.project.backend.pathShare.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RideNotificationHandler extends TextWebSocketHandler {
    private static final Map<String, WebSocketSession> driverSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        driverSessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Example: Drivers could send an "ACK" when they receive a ride request
    }

    public void notifyDriver(String driverId, String rideDetails) throws Exception {
        WebSocketSession session = driverSessions.get(driverId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(rideDetails));
        }
    }
}
