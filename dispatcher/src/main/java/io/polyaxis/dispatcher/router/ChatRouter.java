package io.polyaxis.dispatcher.router;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
@Path("chat")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRouter.class);

    @GET
    @Path("hello")
    public Uni<String> getHello() {
        return Uni.createFrom().item(() -> {
            LOGGER.info("Received request for fruits");
            return "Hello, Fruits!";
        });
    }
}
