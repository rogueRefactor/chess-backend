package com.backend.chess.persistence;


import com.backend.chess.model.Board;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

/**
 * A JPA AttributeConverter to store the Board object as a JSON string in the database.
 */
@Converter(autoApply = true)
public class BoardConverter implements AttributeConverter<Board, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts the Board object into a JSON string for database storage.
     * @param board The Board object to convert.
     * @return A JSON string representation of the board.
     */
    @Override
    public String convertToDatabaseColumn(Board board) {
        try {
            return objectMapper.writeValueAsString(board);
        } catch (JsonProcessingException ex) {
            // In a real application, you'd want more robust error handling.
            throw new RuntimeException("Error converting Board to JSON", ex);
        }
    }

    /**
     * Converts the JSON string from the database back into a Board object.
     * @param dbData The JSON string from the database.
     * @return A Board object.
     */
    @Override
    public Board convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Board.class);
        } catch (IOException ex) {
            throw new RuntimeException("Error converting JSON to Board", ex);
        }
    }
}

