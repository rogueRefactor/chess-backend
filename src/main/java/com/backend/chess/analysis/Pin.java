package com.backend.chess.analysis;

import com.backend.chess.model.Coordinates;

public record Pin(Coordinates pinnedPiecePosition, Coordinates pinningPiecePosition) {
}
