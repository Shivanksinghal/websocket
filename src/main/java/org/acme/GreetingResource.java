package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    private DashboardWebSocket webSocket;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        webSocket.getTotalOrders().incrementAndGet();
        webSocket.broadcast(String.valueOf(webSocket.getTotalOrders()));
        return "Hello RESTEasy";
    }
}
