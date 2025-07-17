package com.backend.chess.model;

public record Move(Coordinates from, Coordinates to, PieceType promotion) {
}
