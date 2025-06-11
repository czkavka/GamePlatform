package projekt.zespolowy.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import projekt.zespolowy.models.EGame;
import projekt.zespolowy.models.Ranking;
import projekt.zespolowy.payload.response.RankingResponse;
import projekt.zespolowy.repository.GameRepository;
import projekt.zespolowy.repository.RankingRepository;

@RestController
@RequestMapping("/api/ranking")
public class GameController {
    
  @Autowired
  private RankingRepository rankingRepository;

  @Autowired
  private GameRepository gameRepository;

  /*
   * Zwraca dla danej gry ranking zapytania musza byc typu -  .../api/ranking?eGame=GAME_TICTACTOE
   */
  @GetMapping
  public List<RankingResponse> getRankingByGame(@RequestParam EGame eGame) {
    var game = gameRepository.findByName(eGame)
            .orElseThrow(() -> new RuntimeException("Nie znaleziono gry"));

    List<Ranking> rankings = rankingRepository.findByGameOrderByScoreDesc(game);
    return rankings.stream()
            .map(r -> new RankingResponse(r.getUser().getUsername(), r.getScore()))
            .collect(Collectors.toList());
}

}
