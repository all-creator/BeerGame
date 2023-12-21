package fu.game.beergame.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.service.GameService;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.view.component.Canvas;
import fu.game.beergame.view.component.Header;

@AnonymousAllowed
@PageTitle("Game | Beer Game")
@Route(value = "", layout = Header.class)
public class GamePage extends Canvas {
    public GamePage(PlayerService playerService, SessionService sessionService, GameService gameService) {
        super(playerService, sessionService, gameService);
    }

    @Override
    protected void addUI() {
        main.add();
    }
}
