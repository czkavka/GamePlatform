package projekt.zespolowy.gameService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projekt.zespolowy.models.Game;
import projekt.zespolowy.models.Ranking;
import projekt.zespolowy.models.User;
import projekt.zespolowy.repository.GameRepository;
import projekt.zespolowy.repository.RankingRepository;

@Service
public class RankingService {
    @Autowired
    private RankingRepository rankingRepo;
    @Autowired 
    private GameRepository gameRepo;

    public void addScore(User user, Game game) {
        Ranking ranking = rankingRepo.findByUserAndGame(user, game)
                .orElseGet(() -> new Ranking(user, game, 0));
        ranking.setScore(ranking.getScore() + 100);
        rankingRepo.save(ranking);
    }

    public List<Ranking> getRanking(Game game) {
        return rankingRepo.findByGameOrderByScoreDesc(game);
    }

    public void initializeUserRanking(User user) {
        List<Game> allGames = gameRepo.findAll();
        for (Game game : allGames) {
            Ranking ranking = new Ranking(user, game, 0);
            rankingRepo.save(ranking);
        }
    }
}