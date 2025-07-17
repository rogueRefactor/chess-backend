package com.backend.chess.analysis;

import java.util.Map;
import java.util.List;

public record AnalysisResult(
        Map<String, Territory> territoryMap,
        Map<String, String> attackedPieces,
        List<Pin> pins
) {
}
