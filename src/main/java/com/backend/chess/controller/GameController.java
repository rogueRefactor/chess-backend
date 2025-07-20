package com.backend.chess.controller;

import com.backend.chess.dto.GameStateDTO;
import com.backend.chess.model.Game;
import com.backend.chess.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Endpoint to create a new chess game.
     * Responds to POST requests at /api/game/new
     *
     * @return A ResponseEntity containing the initial GameStateDTO.
     */
    @PostMapping("/new")
    public ResponseEntity<GameStateDTO> createNewGame() {
        Game newGame = gameService.createNewGame();
        // For a new game, we can create a temporary DTO without the analysis,
        // as the initial state is always the same.
        GameStateDTO gameState = new GameStateDTO(
                newGame.getId(),
                newGame.getBoard().getSquares(),
                newGame.getCurrentPlayer(),
                newGame.getStatus(),
                newGame.getMoveHistory(),
                null // No analysis needed for the starting position
        );
        return ResponseEntity.ok(gameState);
    }

    /**
     * Endpoint to get the state of an existing game.
     * Responds to GET requests at /api/game/{gameId}
     *
     * @param gameId The ID of the game to retrieve.
     * @return A ResponseEntity containing the current GameStateDTO for the requested game.
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateDTO> getGame(@PathVariable String gameId) {
        // Here we would ideally run the analysis on the current state before returning.
        // For now, we'll return the state without it.
        // This will be updated when we fully implement the makeMove flow.
        Game game = gameService.getGame(gameId);
        GameStateDTO gameState = new GameStateDTO(
                game.getId(),
                game.getBoard().getSquares(),
                game.getCurrentPlayer(),
                game.getStatus(),
                game.getMoveHistory(),
                null // Placeholder for analysis
        );
        return ResponseEntity.ok(gameState);
    }
}
