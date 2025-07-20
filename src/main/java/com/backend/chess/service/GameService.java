package com.backend.chess.service;

import com.backend.chess.analysis.AnalysisResult;
import com.backend.chess.dto.GameStateDTO;
import com.backend.chess.dto.MoveDto;
import com.backend.chess.model.Coordinates;
import com.backend.chess.model.Game;
import com.backend.chess.model.Move;
import com.backend.chess.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class GameService {

    private final GameRepository gameRepository;
    private final AnalysisService analysisService;

    @Autowired
    public GameService(GameRepository gameRepository, AnalysisService analysisService) {
        this.gameRepository = gameRepository;
        this.analysisService = analysisService;
    }

    /**
     * Creates a new game, saves it to the database, and returns it.
     * @return The newly created Game object.
     */
    @Transactional
    public Game createNewGame() {
        Game game = new Game();
        return gameRepository.save(game);
    }

    /**
     * Retrieves a game by its ID.
     * @param gameId The ID of the game to find.
     * @return The found Game object.
     * @throws IllegalArgumentException if no game is found with the given ID.
     */
    public Game getGame(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));
    }

    /**
     * Processes a player's move.
     * This involves validating the move, updating the game state, saving it,
     * running the analysis, and returning the new state.
     *
     * @param gameId The ID of the game where the move is being made.
     * @param moveDTO The move data from the client.
     * @return A GameStateDTO representing the new state of the game after the move.
     */
    @Transactional
    public GameStateDTO makeMove(String gameId, MoveDto moveDTO) {
        Game game = getGame(gameId);

        // TODO: Add full validation logic here.
        // - Check if it's the correct player's turn.
        // - Check if the move is pseudo-legal for the piece.
        // - Check if the move would leave the player's own king in check.

        // Convert DTO to domain objects
        Move move = convertDtoToMove(moveDTO);
        String moveNotation = moveDTO.getFrom() + moveDTO.getTo();

        // Apply the move to the game state
        game.applyMove(move);
        game.addMoveToHistory(moveNotation);

        // TODO: Update game status (check, checkmate, stalemate) based on the new position.

        // Save the updated game state to the database
        Game updatedGame = gameRepository.save(game);

        // Run the analysis on the new board state
        AnalysisResult analysisResult = analysisService.analyzeBoard(updatedGame.getBoard());

        // Create and return the DTO for the new game state
        return new GameStateDTO(
                updatedGame.getId(),
                updatedGame.getBoard().getSquares(),
                updatedGame.getCurrentPlayer(),
                updatedGame.getStatus(),
                updatedGame.getMoveHistory(),
                analysisResult
        );
    }

    /**
     * Converts a MoveDTO from the client into a domain Move object.
     */
    private Move convertDtoToMove(MoveDto moveDTO) {
        Coordinates from = algebraicToCoordinates(moveDTO.getFrom());
        Coordinates to = algebraicToCoordinates(moveDTO.getTo());
        return new Move(from, to, moveDTO.getPromotion());
    }

    /**
     * Converts a square in algebraic notation (e.g., "e4") to a Coordinates object.
     */
    private Coordinates algebraicToCoordinates(String algebraic) {
        if (algebraic == null || algebraic.length() != 2) {
            throw new IllegalArgumentException("Invalid algebraic notation: " + algebraic);
        }
        int x = algebraic.charAt(0) - 'a';
        int y = algebraic.charAt(1) - '1';
        return new Coordinates(x, y);
    }
}
