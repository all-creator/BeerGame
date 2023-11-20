package fu.game.beergame.service;


import fu.game.beergame.model.Player;
import fu.game.beergame.model.Session;
import fu.game.beergame.repository.PlayerRepository;
import fu.game.beergame.utils.Broadcaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void ready(Player player, Session session) {
        player.setReady(true);
        session.updatePlayer(player);
        save(player);
        log.info("Player {} is ready", player.getUsername());
        Broadcaster.broadcast("Player " + player.getUsername() + " is ready");
    }

    public void leave(Player player, Session session) {
        session.disconnectPlayer(player);
        save(player);
        log.info("Player {} left the room", player.getUsername());
        Broadcaster.broadcast("Player " + player.getUsername() + " left the room");
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public Player get(String username) {
        return playerRepository.findByUsername(username).orElseThrow();
    }

    public Player find(String username) {
        return playerRepository.findByUsername(username).orElse(new Player(username));
    }
}
