package projekt.zespolowy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekt.zespolowy.models.EGame;
import projekt.zespolowy.models.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByName(EGame name);
}