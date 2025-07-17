package com.backend.chess.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class Game {
    private String id;
    private Board board;
    @Setter
    private PlayerColor currentPlayer;
    @Setter
    private GameStatus status;
    @Setter
    private List<String> moveHistory;  // storing moves in algebraic notations

    public Game(){
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



}
