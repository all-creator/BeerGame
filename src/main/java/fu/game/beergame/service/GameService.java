package fu.game.beergame.service;

import fu.game.beergame.repository.GameRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
}
