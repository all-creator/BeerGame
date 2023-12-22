package fu.game.beergame.view.component;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import fu.game.beergame.model.Game;
import fu.game.beergame.model.Player;
import fu.game.beergame.service.GameService;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.NotificationUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Canvas extends VerticalLayout implements HasUrlParameter<String> {
    protected final transient PlayerService playerService;
    protected final transient SessionService sessionService;
    protected final transient GameService gameService;
    protected transient Game game;
    protected transient Player player;

    // Component
    protected HorizontalLayout header = new HorizontalLayout();
    protected VerticalLayout main = new VerticalLayout();

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        setSizeFull();
        game = sessionService.loadGame(parameter);
        event.getLocation().getQueryParameters().getParameters("player").stream().findFirst().ifPresentOrElse(
                p -> player = playerService.get(p),
                () -> NotificationUtils.notifyError("Player not found")
        );
        configureUI();
        addUI();
    }

    protected abstract void addUI();
    protected void configureUI() {
        loadHeader();
        loadMainScreen();
    }

    protected void loadHeader() {
        header.setWidthFull();
        header.setSpacing(false);
        header.setMargin(false);
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setHeight("10%");

        add(header);
    }
    protected void loadMainScreen() {
        main.setSizeFull();
        main.setAlignItems(Alignment.CENTER);
        main.setJustifyContentMode(JustifyContentMode.BETWEEN);
        main.setSpacing(false);
        main.setMargin(false);

        add(main);
    }

    protected static VerticalLayout getCard() {
        final VerticalLayout cardOutput = new VerticalLayout();
        cardOutput.setWidth("35%");
        cardOutput.addClassName("card");
        cardOutput.setHeightFull();
        cardOutput.setMargin(true);
        cardOutput.setAlignItems(Alignment.CENTER);
        cardOutput.setJustifyContentMode(JustifyContentMode.CENTER);
        return cardOutput;
    }
}
