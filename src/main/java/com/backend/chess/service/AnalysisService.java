package com.backend.chess.service;

import com.backend.chess.analysis.AnalysisResult;
import com.backend.chess.analysis.Pin;
import com.backend.chess.analysis.Territory;
import com.backend.chess.model.Board;
import com.backend.chess.model.Coordinates;
import com.backend.chess.model.Piece;
import com.backend.chess.model.PlayerColor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    public AnalysisResult analyzeBoard(Board board) {
        Map<Coordinates, Territory> territoryMap = initializeTerritoryMap();
        Map<String, String> attackedPieces = new HashMap<>();
        List<Pin> pins = new ArrayList<>();

        // This is a placeholder for the complex pin calculation logic.
        // We will implement this in a later step.
        // calculatePins(board, pins);

        // Iterate through every square to calculate attacks and territory
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Coordinates currentCoords = new Coordinates(x, y);
                Piece piece = board.getPieceAt(currentCoords);
                if (piece != null) {
                    // Get all squares this piece attacks
                    List<Coordinates> attackedSquares = getAttackedSquaresForPiece(piece, currentCoords, board);

                    for (Coordinates attackedCoord : attackedSquares) {
                        // Update the territory map
                        Territory currentTerritory = territoryMap.get(attackedCoord);
                        int whiteAttackers = currentTerritory.whiteAttackers();
                        int blackAttackers = currentTerritory.blackAttackers();

                        if (piece.color() == PlayerColor.WHITE) {
                            whiteAttackers++;
                        } else {
                            blackAttackers++;
                        }

                        PlayerColor controller = (whiteAttackers > blackAttackers) ? PlayerColor.WHITE :
                                (blackAttackers > whiteAttackers) ? PlayerColor.BLACK : null;

                        territoryMap.put(attackedCoord, new Territory(controller, whiteAttackers, blackAttackers));

                        // Check if an enemy piece is on the attacked square
                        Piece targetPiece = board.getPieceAt(attackedCoord);
                        if (targetPiece != null && targetPiece.color() != piece.color()) {
                            attackedPieces.put(coordinatesToAlgebraic(attackedCoord), piece.color().name());
                        }
                    }
                }
            }
        }

        // Convert the territory map keys from Coordinates to String for the DTO
        Map<String, Territory> territoryMapForDto = new HashMap<>();
        for (Map.Entry<Coordinates, Territory> entry : territoryMap.entrySet()) {
            territoryMapForDto.put(coordinatesToAlgebraic(entry.getKey()), entry.getValue());
        }

        return new AnalysisResult(territoryMapForDto, attackedPieces, pins);
    }

    /**
     * Gets all squares that a given piece attacks.
     * This method delegates to piece-specific helpers.
     */
    private List<Coordinates> getAttackedSquaresForPiece(Piece piece, Coordinates position, Board board) {
        // In a real chess engine, this would be more complex, handling captures vs. non-capturing moves.
        // For our analysis, we care about any square the piece can "see".
        switch (piece.type()) {
            // case PAWN: return getAttackedSquaresForPawn(piece, position, board);
            // case ROOK: return getAttackedSquaresForRook(piece, position, board);
            // case KNIGHT: return getAttackedSquaresForKnight(piece, position, board);
            // case BISHOP: return getAttackedSquaresForBishop(piece, position, board);
            // case QUEEN: return getAttackedSquaresForQueen(piece, position, board);
            // case KING: return getAttackedSquaresForKing(piece, position, board);
            default: return new ArrayList<>(); // Default to empty for now
        }
    }

    // --- Placeholder methods for piece-specific attack logic ---
    // We will implement the logic for these in subsequent steps.

    // private List<Coordinates> getAttackedSquaresForPawn(...) { ... }
    // private List<Coordinates> getAttackedSquaresForRook(...) { ... }
    // private List<Coordinates> getAttackedSquaresForKnight(...) { ... }
    // private List<Coordinates> getAttackedSquaresForBishop(...) { ... }
    // private List<Coordinates> getAttackedSquaresForQueen(...) { ... }
    // private List<Coordinates> getAttackedSquaresForKing(...) { ... }


    /**
     * Initializes an 8x8 map where each square has 0 attackers.
     *
     * @return A map where keys are Coordinates and values are initial Territory objects.
     */
    private Map<Coordinates, Territory> initializeTerritoryMap() {
        Map<Coordinates, Territory> map = new HashMap<>();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                map.put(new Coordinates(x, y), new Territory(null, 0, 0));
            }
        }
        return map;
    }

    /**
     * Converts a Coordinates object to standard algebraic notation (e.g., "a1", "h8").
     *
     * @param coords The coordinates to convert.
     * @return A string in algebraic notation.
     */
    private String coordinatesToAlgebraic(Coordinates coords) {
        char file = (char) ('a' + coords.x());
        char rank = (char) ('1' + coords.y());
        return "" + file + rank;
    }
}
