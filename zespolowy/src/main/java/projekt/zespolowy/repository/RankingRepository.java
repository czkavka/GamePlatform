package projekt.zespolowy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projekt.zespolowy.models.Game;
import projekt.zespolowy.models.Ranking;
import projekt.zespolowy.models.User;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByUserAndGame(User user, Game game);
    
    List<Ranking> findByGameOrderByScoreDesc(Game game);
}
