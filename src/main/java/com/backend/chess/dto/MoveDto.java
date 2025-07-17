package com.backend.chess.dto;

import com.backend.chess.model.PieceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveDto {
    private String from;
    private String to;
    private PieceType promotion; // optional: in case of promotion

}
