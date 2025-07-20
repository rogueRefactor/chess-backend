package com.backend.chess.controller;

import com.backend.chess.dto.GameStateDTO;
import com.backend.chess.dto.MoveDto;
import com.backend.chess.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameSocketController {
    private final GameService gameService;

    @Autowired
    public GameSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles a move sent by a player over the WebSocket connection.
     *
     * @param gameId The ID of the game, extracted from the destination path.
     * @param moveDTO The move data sent by the client.
     * @return The new GameStateDTO which will be broadcast to all subscribers of the game's topic.
     */
    @MessageMapping("/game/{gameId}/move") // Listens for messages sent to this destination
    @SendTo("/topic/game/{gameId}")       // Broadcasts the return value to this topic
    public GameStateDTO handleMove(@DestinationVariable String gameId, MoveDto moveDTO) {
        // The gameService handles all the logic: validation, state update, analysis, and saving.
        return gameService.makeMove(gameId, moveDTO);
    }
}
