package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import io.quarkus.scheduler.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/dashboard/{clientId}")
@ApplicationScoped
public class DashboardWebSocket {

    private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private AtomicInteger totalOrders = new AtomicInteger();

    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        sessions.put(clientId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clientId") String clientId) {
        sessions.remove(clientId);
    }

    @OnError
    public void onError(Session session, @PathParam("clientId") String clientId, Throwable throwable) {
        sessions.remove(clientId);
    }

    @Scheduled(every="5s")
    void increment() {
        if (sessions != null) {
            totalOrders.incrementAndGet();
            broadcast(String.valueOf(totalOrders));
        }
    }

    public AtomicInteger getTotalOrders() {
        return totalOrders;
    }

    public void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

}