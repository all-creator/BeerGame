package fu.game.beergame.service;


import fu.game.beergame.model.Player;
import fu.game.beergame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void save(Player player) {
        playerRepository.save(player);
    }

    public Player get(String username) {
        return playerRepository.findByUsername(username).orElseThrow();
    }
}
