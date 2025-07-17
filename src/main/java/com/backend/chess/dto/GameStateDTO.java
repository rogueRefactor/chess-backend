package com.backend.chess.dto;

import com.backend.chess.model.GameStatus;
import com.backend.chess.model.Piece;
import com.backend.chess.model.PlayerColor;

import java.util.List;

public record GameStateDTO(
        String gameId,
        Piece[][] board,
        PlayerColor currentPlayer,
        GameStatus status,
        List<String> moveHistory,
        AnalysisResult analysis
) {
}
