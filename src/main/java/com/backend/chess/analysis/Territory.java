package com.backend.chess.analysis;

import com.backend.chess.model.PlayerColor;

public record Territory(PlayerColor controller, int whiteAttackers, int blackAttackers) {
}
