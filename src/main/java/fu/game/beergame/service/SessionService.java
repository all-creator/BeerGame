package fu.game.beergame.service;

import fu.game.beergame.model.Player;
import fu.game.beergame.model.Session;
import fu.game.beergame.repository.SessionRepository;
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

    public Session getSession(String uuid) {
        return sessionRepository.findById(UUID.fromString(uuid)).orElseThrow();
    }

    public Session getSession(int code) {
        return sessionRepository.findByCode(code).orElse(null);
    }

    public void save(Session session) {
        sessionRepository.save(session);
    }
}
