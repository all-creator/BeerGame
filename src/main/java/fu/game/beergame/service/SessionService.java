package fu.game.beergame.service;

import fu.game.beergame.model.Player;
import fu.game.beergame.model.Session;
import fu.game.beergame.repository.SessionRepository;
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.utils.CodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final PlayerService playerService;

    public Session createSession(Player player) {
        var s = new Session(CodeUtils.getCode(), player);
        player.setSession(s);
        sessionRepository.save(s);
        playerService.save(player);
        return s;
    }

    public void connectToSession(Session session, Player player) {
        session.connectPlayer(player);
        playerService.save(player);
        sessionRepository.save(session);
        Broadcaster.broadcast("Player " + player.getUsername() + " joined to session " + session.getId());
    }

    public Session getSession(String uuid) {
        return sessionRepository.findById(UUID.fromString(uuid)).orElseThrow();
    }

    public Session getSession(int code) {
        return sessionRepository.findByCode(code).orElseThrow();
    }

    public void save(Session session) {
        sessionRepository.save(session);
    }
}
