package fu.game.beergame.service;

import fu.game.beergame.common.SessionStatus;
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
        log.info("Player {} created session {}", player.getUsername(), s.getId());
        return s;
    }

    public void connectToSession(Session session, Player player) {
        session.connectPlayer(player);
        playerService.save(player);
        sessionRepository.save(session);
        log.info("Player {} joined to session {}", player.getUsername(), session.getId());
        Broadcaster.broadcast("Player " + player.getUsername() + " joined to session " + session.getId());
    }

    public void closeSession(Session session) {
        var players = session.getPlayers();
        session.close();
        sessionRepository.save(session);
        players.forEach(playerService::save);
        log.info("Session {} closed", session.getId());
        Broadcaster.broadcast("Session " + session.getId() + " closed");
    }

    public void openSession(Session session, Player player) {
        session.getPlayers().add(player);
        player.setSession(session);
        session.setStatus(SessionStatus.READY_TO_CONNECT);
        sessionRepository.save(session);
        playerService.save(player);
        log.info("Session {} opened", session.getId());
        Broadcaster.broadcast("Session " + session.getId() + " opened");
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
