package fu.game.beergame.view;

import com.vaadin.flow.component.ClickEvent;
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
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.view.component.GameLobby;
import fu.game.beergame.view.component.Header;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Getter
@AnonymousAllowed
@PageTitle("Wait Game | Beer Game")
@Route(value = "wait", layout = Header.class)
public class WaitGamePage extends GameLobby {
    // Component's
    private FormLayout formLayout;
    private Button readyButton;
    private Button leaveButton;
    private final Select<TypeOfPlayer> sel = new Select<>();
    public WaitGamePage(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);
    }

    @Override
    protected void addUI() {
        // Add and configure UI
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);

        formLayout = new FormLayout(new H1("Game " + session.getCode()));
        setAlignSelf(Alignment.CENTER, formLayout);

        readyButton = new Button("Ready", this::onReady);
        readyButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        readyButton.setDisableOnClick(true);

        leaveButton = new Button("Leave", this::onLeave);
        leaveButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        formLayout.add(sel, readyButton, leaveButton);

        add(formLayout);

        // Register Broadcaster
        broadcasterRegistration = Broadcaster.register(newMessage -> getUI().ifPresent(ui -> ui.access(() -> {
            log.info("New Broadcaster message: {}, for session: {}", newMessage, session);
            sel.setItems(Arrays.stream(TypeOfPlayer.values()).filter(t -> !sessionService.getSession(gameUuid).getPlayers().stream().map(Player::getType).toList().contains(t)).toList());
            log.info(sel.getListDataView().getItems().toList().toString());
        })));
    }

    @Override
    protected Select<TypeOfPlayer> getSelect() {
        return sel;
    }

    private void onLeave(ClickEvent<Button> event) {
        playerService.leave(player, session);
        sessionService.save(session);
        getUI().ifPresent(ui -> ui.navigate(""));
    }

    private void onReady(ClickEvent<Button> event) {
        player.setType(sel.getValue());
        leaveButton.setEnabled(false);
        sel.setEnabled(false);
        playerService.ready(player, session);
        sessionService.save(session);
    }
}
