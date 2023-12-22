package fu.game.beergame.service;

import fu.game.beergame.model.Game;
import fu.game.beergame.repository.GameRepository;
import fu.game.beergame.repository.GameStreamRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameStreamRepository gameStreamRepository;

    public void save(Game game) {
        gameRepository.save(game);
    }

    public Game getFetch(Game game) {
        return gameRepository.findFetch(game.getId());
    }
}
