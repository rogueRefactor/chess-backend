package com.backend.chess.model;

import lombok.Getter;

import java.util.Arrays;

public class Board {
    @Getter
    private Piece[][] squares;

    //constructors
    public Board() {
        squares = new Piece[8][8];
        setupInitialPositions();

    }

    public Board(Piece[][] squares) {
        this.squares = squares;
    }

    //public functions
    /**
     * Gets the piece at a given coordinate.
     *
     * @param coords The coordinates of the square.
     * @return The Piece at the given square, or null if the square is empty or out of bounds.
     */
    public Piece getPieceAt(Coordinates coords) {
        if (coords.isOutOfBounds()) {
            return null;
        }
        return squares[coords.y()][coords.x()];
    }

    /**
     * Places a piece on a given square. Used for making moves.
     *
     * @param coords The coordinates of the square.
     * @param piece The piece to place on the square. Can be null to clear the square.
     */
    public void setPieceAt(Coordinates coords, Piece piece) {
        if (!coords.isOutOfBounds()) {
            squares[coords.y()][coords.x()] = piece;
        }
    }

    public Board copy() {
        Piece[][] newSquares = new Piece[8][];
        for (int i = 0; i < 8; i++) {
            newSquares[i] = Arrays.copyOf(squares[i], 8);
        }
        return new Board(newSquares);
    }

    //private functions



    private void setupInitialPositions() {
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new Piece(PieceType.PAWN, PlayerColor.WHITE);
            squares[6][i] = new Piece(PieceType.PAWN, PlayerColor.BLACK);
        }

        // Place White pieces
        squares[0][0] = new Piece(PieceType.ROOK, PlayerColor.WHITE);
        squares[0][7] = new Piece(PieceType.ROOK, PlayerColor.WHITE);
        squares[0][1] = new Piece(PieceType.KNIGHT, PlayerColor.WHITE);
        squares[0][6] = new Piece(PieceType.KNIGHT, PlayerColor.WHITE);
        squares[0][2] = new Piece(PieceType.BISHOP, PlayerColor.WHITE);
        squares[0][5] = new Piece(PieceType.BISHOP, PlayerColor.WHITE);
        squares[0][3] = new Piece(PieceType.QUEEN, PlayerColor.WHITE);
        squares[0][4] = new Piece(PieceType.KING, PlayerColor.WHITE);

        // Place Black pieces
        squares[7][0] = new Piece(PieceType.ROOK, PlayerColor.BLACK);
        squares[7][7] = new Piece(PieceType.ROOK, PlayerColor.BLACK);
        squares[7][1] = new Piece(PieceType.KNIGHT, PlayerColor.BLACK);
        squares[7][6] = new Piece(PieceType.KNIGHT, PlayerColor.BLACK);
        squares[7][2] = new Piece(PieceType.BISHOP, PlayerColor.BLACK);
        squares[7][5] = new Piece(PieceType.BISHOP, PlayerColor.BLACK);
        squares[7][3] = new Piece(PieceType.QUEEN, PlayerColor.BLACK);
        squares[7][4] = new Piece(PieceType.KING, PlayerColor.BLACK);
    }

}

