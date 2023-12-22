package fu.game.beergame.service;

import fu.game.beergame.common.AccumulatorType;
import fu.game.beergame.common.BroadcasterCommand;
import fu.game.beergame.common.SessionStatus;
import fu.game.beergame.model.Game;
import fu.game.beergame.model.Player;
import fu.game.beergame.model.Session;
import fu.game.beergame.model.modeling.Accumulator;
import fu.game.beergame.model.modeling.GameStream;
import fu.game.beergame.model.modeling.StreamItem;
import fu.game.beergame.repository.AccumulatorRepository;
import fu.game.beergame.repository.GameStreamRepository;
import fu.game.beergame.repository.SessionRepository;
import fu.game.beergame.repository.StreamItemRepository;
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
    private final GameService gameService;
    private final AccumulatorRepository accumulatorRepository;
    private final PlayerService playerService;
    private final StreamItemRepository streamItemRepository;
    private final GameStreamRepository gameStreamRepository;

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
        Broadcaster.broadcast(BroadcasterCommand.PLAYER_JOINED.getCommand() +"Player " + player.getUsername() + " joined to session " + session.getId());
    }

    public void closeSession(Session session) {
        var players = session.getPlayers();
        session.close();
        sessionRepository.save(session);
        players.forEach(player -> {
            player.setReady(false);
            player.setType(null);
            playerService.save(player);
        });
        log.info("Session {} closed", session.getId());
        Broadcaster.broadcast(BroadcasterCommand.CONSOLE_MESSAGE.getCommand() +"Session " + session.getId() + " closed");
    }

    public Game loadGame(String sessionId) {
        return sessionRepository.findById(UUID.fromString(sessionId)).orElseThrow().getGame();
    }

    public void createGame(Session session) {
        // init game
        Game game = new Game(session);
        game.setCons(CodeUtils.R.nextInt(19) + 1);
        session.setGame(game);
        gameService.save(game);
        sessionRepository.save(session);
        // init accumulators
        session.getPlayers().forEach(player -> {
            game.addAccumulator(accumulatorRepository.save(new Accumulator(player, game, AccumulatorType.RESOURCE.getName(), 12)));
            game.addAccumulator(accumulatorRepository.save(new Accumulator(player, game, AccumulatorType.WEEK.getName(), 1)));
            game.addAccumulator(accumulatorRepository.save(new Accumulator(player, game, AccumulatorType.OVERFLOW.getName(), 0)));
            game.addAccumulator(accumulatorRepository.save(new Accumulator(player, game, AccumulatorType.REQUEST.getName(), 0)));
        });
        // init streams
        // newWeek stream
        game.addStream(gameStreamRepository.save(new GameStream("newWeek", () -> {}, BroadcasterCommand.NEW_WEEK.getCommand() + game.getCons(), BroadcasterCommand.PAGE_RELOAD.getCommand(), game)));
        // newTurn stream
        game.addStream(gameStreamRepository.save(new GameStream("newTurn", () -> {}, null, BroadcasterCommand.NEXT_TURN.getCommand(), game)));
        // init items
        game.addStreamItem(streamItemRepository.save(new StreamItem("seller_shelf_in_1", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("seller_shelf_out_1", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("seller_shelf_in_2", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("seller_shelf_out_2", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("provider_shelf_in_1", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("provider_shelf_out_1", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("provider_shelf_in_2", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("provider_shelf_out_2", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("wholesaler_shelf_in_1", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("wholesaler_shelf_out_1", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("wholesaler_shelf_in_2", game)));
        game.addStreamItem(streamItemRepository.save(new StreamItem("wholesaler_shelf_out_2", game)));
        game.getItems().stream().filter(i -> i.getName().endsWith("2")).forEach(item -> item.addAccumulator(accumulatorRepository.save(new Accumulator(item, game, AccumulatorType.REQUEST.getName(), 0))));
        game.getItems().stream().filter(i -> i.getName().endsWith("1")).forEach(item -> item.addAccumulator(accumulatorRepository.save(new Accumulator(item, game, AccumulatorType.REQUEST.getName(), 4))));
        gameService.save(game);
    }

    public void openSession(Session session, Player player) {
        session.updatePlayer(player);
        player.setSession(session);
        session.setStatus(SessionStatus.READY_TO_CONNECT);
        sessionRepository.save(session);
        playerService.save(player);
        log.info("Session {} opened", session.getId());
        Broadcaster.broadcast(BroadcasterCommand.CONSOLE_MESSAGE.getCommand() +"Session " + session.getId() + " opened");
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
