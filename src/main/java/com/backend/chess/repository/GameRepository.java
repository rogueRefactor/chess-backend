package com.backend.chess.repository;

import com.backend.chess.model.Game;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
}
