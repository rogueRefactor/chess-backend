package com.backend.chess.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * Configures the message broker.
     * @param registry The registry for configuring the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enables a simple in-memory message broker to carry messages back to the client
        // on destinations prefixed with "/topic".
        registry.enableSimpleBroker("/topic");

        // Designates the "/app" prefix for messages that are bound for
        // @MessageMapping-annotated methods in our controllers.
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP endpoints, mapping each to a specific URL and enabling
     * SockJS fallback options.
     * @param registry The registry for STOMP endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the "/ws" endpoint, which is the URL the client will connect to.
        // withSockJS() provides a fallback for browsers that don't support WebSockets.
        registry.addEndpoint("/ws").withSockJS();
    }
}
