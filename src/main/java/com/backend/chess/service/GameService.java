package com.backend.chess.service;

import com.backend.chess.analysis.AnalysisResult;
import com.backend.chess.analysis.Territory;
import com.backend.chess.dto.GameStateDTO;
import com.backend.chess.dto.MoveDto;
import com.backend.chess.model.*;
import com.backend.chess.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
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
        Move move = convertDtoToMove(moveDTO);
        if (!isMoveLegal(game, move)) {
            // If the move is illegal, we throw an exception.
            // In a real application, we might send a specific error message back to the player.
            throw new IllegalArgumentException("Illegal move: " + moveDTO.getFrom() + " to " + moveDTO.getTo());
        }


        // TODO: Add full validation logic here.
        // - Check if it's the correct player's turn.
        // - Check if the move is pseudo-legal for the piece.
        // - Check if the move would leave the player's own king in check.

        // Convert DTO to domain objects
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
     * Checks if a move is legal in the context of the current game.
     */
    private boolean isMoveLegal(Game game, Move move) {
        Piece pieceToMove = game.getBoard().getPieceAt(move.from());

        // 1. Basic checks: Is there a piece? Is it the correct player's turn?
        if (pieceToMove == null || pieceToMove.color() != game.getCurrentPlayer()) {
            return false;
        }

        // TODO: Add pseudo-legal move checks (e.g., is this a valid knight move?)
        // For now, we assume the client sends valid-looking moves.

        // 2. The most important check: Does this move leave the king in check?
        // We simulate the move on a temporary board to find out.
        Board boardAfterMove = game.getBoard().copy();
        boardAfterMove.setPieceAt(move.to(), pieceToMove);
        boardAfterMove.setPieceAt(move.from(), null);

        // Analyze the board state *after* the move.
        AnalysisResult analysisAfterMove = analysisService.analyzeBoard(boardAfterMove);
        Coordinates kingPosition = analysisService.findKing(game.getCurrentPlayer(), boardAfterMove);

        // Check if the king's square is attacked by the opponent.
        Territory kingTerritory = analysisAfterMove.territoryMap().get(coordinatesToAlgebraic(kingPosition));
        if (game.getCurrentPlayer() == PlayerColor.WHITE) {
            return kingTerritory.blackAttackers() == 0;
        } else {
            return kingTerritory.whiteAttackers() == 0;
        }
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
     * Updates the game's status based on the analysis of the new position.
     */
    private void updateGameStatus(Game game, AnalysisResult analysisResult) {
        PlayerColor opponentColor = (game.getCurrentPlayer() == PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;
        Coordinates opponentKingPosition = analysisService.findKing(opponentColor, game.getBoard());
        Territory opponentKingTerritory = analysisResult.territoryMap().get(coordinatesToAlgebraic(opponentKingPosition));

        boolean isOpponentInCheck = (opponentColor == PlayerColor.WHITE && opponentKingTerritory.blackAttackers() > 0) ||
                (opponentColor == PlayerColor.BLACK && opponentKingTerritory.whiteAttackers() > 0);

        if (isOpponentInCheck) {
            // TODO: Here we would check if it's checkmate.
            game.setStatus(GameStatus.CHECK);
        } else {
            // TODO: Here we would check if it's stalemate.
            game.setStatus(GameStatus.IN_PROGRESS);
        }
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
    private String coordinatesToAlgebraic(Coordinates coords) {
        char file = (char) ('a' + coords.x());
        char rank = (char) ('1' + coords.y());
        return "" + file + rank;
    }
}
