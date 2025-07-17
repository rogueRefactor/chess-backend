package com.backend.chess.model;

public record Coordinates(Integer x, Integer y) {
    public boolean isOutOfBounds() {
        return x < 0 || x > 7 || y < 0 || y > 7;
    }
}
