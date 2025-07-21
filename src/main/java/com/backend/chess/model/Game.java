package com.backend.chess.model;

import com.backend.chess.persistence.BoardConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Game {
    @Id
    private String id;

    @Convert(converter = BoardConverter.class)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Setter
    private PlayerColor currentPlayer;

    @Enumerated(EnumType.STRING)
    @Setter
    private GameStatus status;

    @ElementCollection(fetch = FetchType.EAGER) // Store a collection of basic types
    @CollectionTable(name="game_moves", joinColumns=@JoinColumn(name="game_id"))
    @Column(name="move")
    @Setter
    private List<String> moveHistory;  // storing moves in algebraic notations

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.board = new Board();
        this.currentPlayer = PlayerColor.WHITE;
        this.status = GameStatus.IN_PROGRESS;
        this.moveHistory = new ArrayList<>();
    }

    public void applyMove(Move move) {
        Piece pieceToMove = board.getPieceAt(move.from());
        if (pieceToMove == null) {
            return;
        }

        Piece movedPiece = (move.promotion() != null)
                ? new Piece(move.promotion(), pieceToMove.color())
                : pieceToMove;

        board.setPieceAt(move.to(), movedPiece);
        board.setPieceAt(move.from(), null);

        this.currentPlayer = (this.currentPlayer == PlayerColor.WHITE) ? PlayerColor.BLACK : PlayerColor.WHITE;
    }


    public void addMoveToHistory(String moveNotation) {
        this.moveHistory.add(moveNotation);
    }
}
