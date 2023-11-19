package fu.game.beergame.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.common.TypeOfPlayer;
import fu.game.beergame.model.Player;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.view.component.GameLobby;
import fu.game.beergame.view.component.Header;

@Route(value = "wait", layout = Header.class)
@AnonymousAllowed
@PageTitle("Wait Game | Beer Game")
public class WaitGamePage extends GameLobby {
    public WaitGamePage(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);
    }

    @Override
    protected void addUI() {
        session = sessionService.getSession(gameUuid);
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        FormLayout formLayout = new FormLayout(new H1("Game " + session.getCode()));
        Select<TypeOfPlayer> sel = new Select<>();
        var createButton = new Button("Ready", e -> onPlayerConnected(new Player()));
        sel.setItems(TypeOfPlayer.values());
        createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        formLayout.add(sel, createButton);
        setAlignSelf(Alignment.CENTER, formLayout);
        add(formLayout);
    }
}
