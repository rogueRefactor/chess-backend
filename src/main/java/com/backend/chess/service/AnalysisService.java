package com.backend.chess.service;

import com.backend.chess.analysis.AnalysisResult;
import com.backend.chess.analysis.Pin;
import com.backend.chess.analysis.Territory;
import com.backend.chess.model.*;
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

    private List<Pin> calculatePins(Board board) {
        List<Pin> pins = new ArrayList<>();
        // We need to check for pins for both White and Black kings.
        pins.addAll(findPinsForColor(PlayerColor.WHITE, board));
        pins.addAll(findPinsForColor(PlayerColor.BLACK, board));
        return pins;
    }

    private List<Pin> findPinsForColor(PlayerColor color, Board board) {
        List<Pin> pins = new ArrayList<>();
        Coordinates kingPosition = findKing(color, board);
        if (kingPosition == null) return pins; // Should not happen in a legal game

        int[][] directions = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, // Rook directions
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop directions
        };

        for (int[] dir : directions) {
            Coordinates potentialPin = null;
            Coordinates current = new Coordinates(kingPosition.x() + dir[0], kingPosition.y() + dir[1]);

            while (!current.isOutOfBounds()) {
                Piece pieceOnRay = board.getPieceAt(current);
                if (pieceOnRay != null) {
                    if (pieceOnRay.color() == color) {
                        // It's a friendly piece. Is it the first one we've seen on this line?
                        if (potentialPin == null) {
                            potentialPin = current;
                        } else {
                            // This is the second friendly piece, so no pin is possible on this line.
                            break;
                        }
                    } else {
                        // It's an enemy piece. Does it cause a pin?
                        if (potentialPin != null) {
                            // We have a friendly piece between the king and this enemy piece.
                            // Check if the enemy piece is a sliding piece that can attack along this line.
                            PieceType type = pieceOnRay.type();
                            boolean isRookLike = (type == PieceType.ROOK || type == PieceType.QUEEN);
                            boolean isBishopLike = (type == PieceType.BISHOP || type == PieceType.QUEEN);
                            boolean isStraight = dir[0] == 0 || dir[1] == 0;
                            boolean isDiagonal = Math.abs(dir[0]) == Math.abs(dir[1]);

                            if ((isStraight && isRookLike) || (isDiagonal && isBishopLike)) {
                                // It's a pin!
                                pins.add(new Pin(potentialPin, current));
                            }
                        }
                        // Whether it caused a pin or not, the line of sight is blocked.
                        break;
                    }
                }
                current = new Coordinates(current.x() + dir[0], current.y() + dir[1]);
            }
        }
        return pins;
    }

    public Coordinates findKing(PlayerColor color, Board board) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Coordinates coords = new Coordinates(x, y);
                Piece piece = board.getPieceAt(coords);
                if (piece != null && piece.type() == PieceType.KING && piece.color() == color) {
                    return coords;
                }
            }
        }
        return null; // Should be unreachable in a valid game
    }


    /**
     * Gets all squares that a given piece attacks.
     * This method delegates to piece-specific helpers.
     */
    private List<Coordinates> getAttackedSquaresForPiece(Piece piece, Coordinates position, Board board) {
        switch (piece.type()) {
            case PAWN:
                return getAttackedSquaresForPawn(piece, position, board);
            case ROOK:
                return getAttackedSquaresForRook(position, board);
            case KNIGHT:
                return getAttackedSquaresForKnight(position);
            case BISHOP:
                return getAttackedSquaresForBishop(position, board);
            case QUEEN:
                return getAttackedSquaresForQueen(position, board);
            case KING:
                return getAttackedSquaresForKing(position);
            default:
                return new ArrayList<>(); // Default to empty for now
        }
    }

    // --- Placeholder methods for piece-specific attack logic ---
    // We will implement the logic for these in subsequent steps.

    private List<Coordinates> getAttackedSquaresForPawn(Piece piece, Coordinates position, Board board) {
        List<Coordinates> attackedSquares = new ArrayList<>();
        int direction = piece.color() == PlayerColor.WHITE ? 1 : -1;
        Coordinates leftAttack = new Coordinates(position.x() - 1, position.y() + direction);
        Coordinates rightAttack = new Coordinates(position.x() + 1, position.y() + direction);
        if (!leftAttack.isOutOfBounds()) {
            attackedSquares.add(leftAttack);
        }
        if (!rightAttack.isOutOfBounds()) {
            attackedSquares.add(rightAttack);
        }
        return attackedSquares;
    }

    private List<Coordinates> getAttackedSquaresForKnight(Coordinates position) {
        List<Coordinates> attackedSquares = new ArrayList<>();
        int[][] moves = {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };

        for (int[] move : moves) {
            Coordinates target = new Coordinates(position.x() + move[0], position.y() + move[1]);
            if (!target.isOutOfBounds()) {
                attackedSquares.add(target);
            }
        }
        return attackedSquares;
    }

    private List<Coordinates> getAttackedSquaresForRook(Coordinates position, Board board) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // Up, Down, Right, Left
        return getSlidingAttackedSquares(position, board, directions);
    }

    private List<Coordinates> getAttackedSquaresForBishop(Coordinates position, Board board) {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // Diagonal directions
        return getSlidingAttackedSquares(position, board, directions);
    }

    private List<Coordinates> getAttackedSquaresForQueen(Coordinates position, Board board) {
        int[][] directions = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, // Rook directions
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop directions
        };
        return getSlidingAttackedSquares(position, board, directions);
    }

    private List<Coordinates> getAttackedSquaresForKing(Coordinates position) {
        List<Coordinates> attackedSquares = new ArrayList<>();
        int[][] moves = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, // Rook directions
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop directions
        };

        for (int[] move : moves) {
            Coordinates target = new Coordinates(position.x() + move[0], position.y() + move[1]);
            if (!target.isOutOfBounds()) {
                attackedSquares.add(target);
            }
        }
        return attackedSquares;
    }


    private List<Coordinates> getSlidingAttackedSquares(Coordinates position, Board board, int[][] directions) {
        List<Coordinates> attackedSquares = new ArrayList<>();
        for (int[] dir : directions) {
            Coordinates current = new Coordinates(position.x() + dir[0], position.y() + dir[1]);
            while (!current.isOutOfBounds()) {
                attackedSquares.add(current);
                // If we hit any piece (friend or foe), the line of sight is blocked.
                if (board.getPieceAt(current) != null) {
                    break;
                }
                current = new Coordinates(current.x() + dir[0], current.y() + dir[1]);
            }
        }
        return attackedSquares;
    }

    /**
     * Generates all pseudo-legal moves for a piece at a given position.
     * "Pseudo-legal" means it follows the piece's movement rules, but doesn't
     * account for leaving the king in check.
     *
     * @param from The starting coordinates of the piece.
     * @param board The current board state.
     * @return A list of possible Move objects.
     */
    public List<Move> generatePseudoLegalMoves(Coordinates from, Board board) {
        Piece piece = board.getPieceAt(from);
        if (piece == null) {
            return new ArrayList<>();
        }

        switch (piece.type()) {
            case PAWN: return generateMovesForPawn(from, piece.color(), board);
            case ROOK: return generateMovesForSlidingPiece(from, piece.color(), board, new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}});
            case KNIGHT: return generateMovesForKnight(from, piece.color(), board);
            case BISHOP: return generateMovesForSlidingPiece(from, piece.color(), board, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case QUEEN: return generateMovesForSlidingPiece(from, piece.color(), board, new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case KING: return generateMovesForKing(from, piece.color(), board);
            default: return new ArrayList<>();
        }
    }

    // --- Private move generation helpers ---

    private List<Move> generateMovesForPawn(Coordinates from, PlayerColor color, Board board) {
        List<Move> moves = new ArrayList<>();
        int direction = (color == PlayerColor.WHITE) ? 1 : -1;
        int startRank = (color == PlayerColor.WHITE) ? 1 : 6;

        // 1. Forward move
        Coordinates oneStep = new Coordinates(from.x(), from.y() + direction);
        if (!oneStep.isOutOfBounds() && board.getPieceAt(oneStep) == null) {
            moves.add(new Move(from, oneStep, null));
            // 2. Double forward move from start
            if (from.y() == startRank) {
                Coordinates twoSteps = new Coordinates(from.x(), from.y() + 2 * direction);
                if (!twoSteps.isOutOfBounds() && board.getPieceAt(twoSteps) == null) {
                    moves.add(new Move(from, twoSteps, null));
                }
            }
        }

        // 3. Captures
        int[] captureCols = {from.x() - 1, from.x() + 1};
        for (int col : captureCols) {
            Coordinates capturePos = new Coordinates(col, from.y() + direction);
            if (!capturePos.isOutOfBounds()) {
                Piece target = board.getPieceAt(capturePos);
                if (target != null && target.color() != color) {
                    moves.add(new Move(from, capturePos, null));
                }
            }
        }
        // TODO: Add en-passant and promotion logic
        return moves;
    }

    private List<Move> generateMovesForKnight(Coordinates from, PlayerColor color, Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] moveOffsets = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] offset : moveOffsets) {
            Coordinates to = new Coordinates(from.x() + offset[0], from.y() + offset[1]);
            if (!to.isOutOfBounds()) {
                Piece target = board.getPieceAt(to);
                if (target == null || target.color() != color) {
                    moves.add(new Move(from, to, null));
                }
            }
        }
        return moves;
    }

    private List<Move> generateMovesForKing(Coordinates from, PlayerColor color, Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] moveOffsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] offset : moveOffsets) {
            Coordinates to = new Coordinates(from.x() + offset[0], from.y() + offset[1]);
            if (!to.isOutOfBounds()) {
                Piece target = board.getPieceAt(to);
                if (target == null || target.color() != color) {
                    moves.add(new Move(from, to, null));
                }
            }
        }
        // TODO: Add castling logic
        return moves;
    }

    private List<Move> generateMovesForSlidingPiece(Coordinates from, PlayerColor color, Board board, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        for (int[] dir : directions) {
            Coordinates current = new Coordinates(from.x() + dir[0], from.y() + dir[1]);
            while (!current.isOutOfBounds()) {
                Piece target = board.getPieceAt(current);
                if (target == null) {
                    moves.add(new Move(from, current, null)); // Can move to empty square
                } else {
                    if (target.color() != color) {
                        moves.add(new Move(from, current, null)); // Can capture enemy piece
                    }
                    break; // Line of sight is blocked
                }
                current = new Coordinates(current.x() + dir[0], current.y() + dir[1]);
            }
        }
        return moves;
    }

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
