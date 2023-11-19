package fu.game.beergame.view.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.shared.Registration;
import fu.game.beergame.common.TypeOfPlayer;
import fu.game.beergame.exceptions.GameException;
import fu.game.beergame.model.Player;
import fu.game.beergame.model.Session;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.NotificationUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public abstract class GameLobby extends Lobby implements HasUrlParameter<String> {
    protected String gameUuid;
    protected Registration broadcasterRegistration;

    protected final Select<TypeOfPlayer> sel = new Select<>();
    protected transient Session session;
    protected transient Player player;

    protected GameLobby(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);
    }

    protected abstract void addUI();

    protected void configureUI() {
        var items = Arrays.stream(TypeOfPlayer.values()).filter(t -> !session.getPlayers().stream().map(Player::getType).toList().contains(t)).toList();
        sel.setItems(items);
        sel.setPlaceholder("Select player type");
        sel.addValueChangeListener(this::onChangeSelect);
        sel.setAutofocus(true);
    }

    protected void onChangeSelect(AbstractField.ComponentValueChangeEvent<Select<TypeOfPlayer>, TypeOfPlayer> e) {
        if (e.getValue() != null) sel.setPrefixComponent(e.getValue().icon);
    }

    protected void onPlayerConnected(Player player) {
        sel.setItems(Arrays.stream(TypeOfPlayer.values()).filter(t -> !session.getPlayers().stream().map(Player::getType).toList().contains(t)).toList());
    }


    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        // Get params
        this.gameUuid = parameter;
        session = sessionService.getSession(gameUuid);
        event.getLocation().getQueryParameters().getParameters("player").stream().findFirst().ifPresentOrElse(
                p -> player = playerService.get(p),
                () -> NotificationUtils.notifyError("Player not found")
        );
        // Configure UI
        configureUI();
        addUI();
    }
}
