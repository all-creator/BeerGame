package fu.game.beergame.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;
import fu.game.beergame.common.AccumulatorType;
import fu.game.beergame.common.BroadcasterCommand;
import fu.game.beergame.common.TypeOfPlayer;
import fu.game.beergame.model.Player;
import fu.game.beergame.model.modeling.Accumulator;
import fu.game.beergame.model.modeling.GameStream;
import fu.game.beergame.repository.AccumulatorRepository;
import fu.game.beergame.service.GameService;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.utils.CodeUtils;
import fu.game.beergame.utils.ComponentUtils;
import fu.game.beergame.utils.NotificationUtils;
import fu.game.beergame.view.component.Canvas;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AnonymousAllowed
@PageTitle("Game | Beer Game")
@Route(value = "/game")
public class GamePage extends Canvas {

    private final VerticalLayout cardInput = getCard();
    private final VerticalLayout cardOutput = getCard();
    private final Span cardInputText = new Span("0");
    private final NumberField cardOutputField = new NumberField();
    private final Button next = new Button("Конец хода", this::endTurn);
    private Registration broadcasterRegistration;
    private final AccumulatorRepository accumulatorRepository;

    public GamePage(PlayerService playerService, SessionService sessionService, GameService gameService, AccumulatorRepository accumulatorRepository) {
        super(playerService, sessionService, gameService);
        this.accumulatorRepository = accumulatorRepository;
    }

    @Override
    protected void addUI() {
        addHeader();
        addMain();
        postLoad();
    }

    private void postLoad() {
        if (game.getCurrentPlayer() != player.getType()) {
            cardOutputField.setEnabled(false);
            next.setEnabled(false);
        } else {
            cardOutputField.setEnabled(true);
            next.setEnabled(true);
            cardOutput.removeClassName("card_anim_right");
        }
    }

    private void addHeader() {
        header.add(new HorizontalLayout(player.getType().create(), new H3(player.getType().displayName)));
        Set<Accumulator> accumulators = game.getAccumulators().stream().filter(a -> a.getPlayer() != null && a.getPlayer().getUsername().equals(player.getUsername())).collect(Collectors.toSet());
        for (Accumulator accumulator : accumulators) {
            header.add(new H4(accumulator.getName() + ": " + accumulator.getValue()));
        }
    }

    private void updateHeader() {
        header.getChildren().forEach(Component::removeFromParent);
        addHeader();
    }

    private void addMain() {
        if (player.getType() == TypeOfPlayer.FABRIC) next.setText("Закончить неделю");
        Player player = game.getCurrentPlayerByType();
        //<theme-editor-local-classname>
        cardInputText.addClassName("canvas-span-1");
        cardInput.add(cardInputText);

        //<theme-editor-local-classname>
        cardOutputField.addClassName("canvas-text-field-1");
        cardOutputField.setPlaceholder("Запросить ресурсы");
        cardOutputField.addClassName("card_input");
        cardOutput.add(cardOutputField);

        //<theme-editor-local-classname>
        next.addClassName("canvas-button-1");
        next.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        final Div leftFooter = new Div();
        leftFooter.addClassName("flex-1");

        final HorizontalLayout rightFooter = new HorizontalLayout(new Span("Ход игрока: " + player.getUsername()), player.getType().create());
        rightFooter.addClassName("flex-1");
        rightFooter.setId("right-footer");
        rightFooter.setAlignItems(Alignment.CENTER);
        rightFooter.setJustifyContentMode(JustifyContentMode.END);
        next.addClassName("flex-1");
        final HorizontalLayout footer = new HorizontalLayout(leftFooter, next, rightFooter);
        footer.setId("footer");
        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);
        footer.addClassName("flex");
        main.add(cardInput, cardOutput, footer);

        if (broadcasterRegistration == null) broadcasterRegistration = Broadcaster.register(newMessage -> getUI().ifPresent(ui -> ui.access(() -> onEvent(newMessage))));
    }

    private void onEvent(String message) {
        log.info("New Broadcaster message: {}, for game: {}", message, game.getSession().getId());
        if (BroadcasterCommand.isCommand(message)) {
            game = gameService.getGameRepository().findById(game.getId()).orElseThrow();
            switch (BroadcasterCommand.getCommand(message)) {
                case NEXT_TURN -> {
                    Player player = game.getCurrentPlayerByType();
                    HorizontalLayout c = (HorizontalLayout) ComponentUtils.getComponent(main, "footer", "right-footer");
                    c.getChildren().forEach(Component::removeFromParent);
                    c.add(new Span("Ход игрока: " + player.getUsername()), player.getType().create());
                    postLoad();
                }
                case NEW_WEEK -> {
                    cardInputText.setText(BroadcasterCommand.getData(message));
                    cardInput.addClassName("card_anim_left");
                    new GameStream("update", this::updateAccumulators,null, null, game).start();
                }
                case PAGE_RELOAD -> updateHeader();
                default -> log.debug("Ignored Broadcaster command: {} as WaitGamePage", message);
            }
        }
    }

    private void endTurn(ClickEvent<Button> event) {
        if (cardOutputField.isEmpty() || cardOutputField.getValue().intValue() == 0) {
            NotificationUtils.notifyError("Запросите ресурсы");
            return;
        }
        cardOutput.addClassName("card_anim_right");
        cardOutputField.setEnabled(false);
        next.setEnabled(false);
        game = gameService.getFetch(game);
        game.getStream("newTurn").setStream(() -> {
            player.getAccumulator(AccumulatorType.REQUEST.getName()).setValue(cardOutputField.getValue().intValue());
            playerService.save(player);
            game.nextPlayer();
            gameService.save(game);
            if (game.getCurrentPlayer() == TypeOfPlayer.SELLER) game.getStream("newWeek").setStream(() -> {
                game.getAllAccumulators("Week").forEach(accumulator -> {
                    accumulator.setValue(accumulator.getValue() + 1);
                    accumulatorRepository.save(accumulator);
                });
                game.setCons(CodeUtils.R.nextInt(20));
                gameService.save(game);
            }).start();
        }).start();
    }

    private void updateAccumulators() {

        Accumulator res = player.getAccumulator(AccumulatorType.RESOURCE.getName());
        if (res.getValue() - game.getCons() >= 0) res.setValue(res.getValue() - game.getCons());
        else {
            res.setValue(0);
            Accumulator overflow = game.getCurrentPlayerByType().getAccumulator(AccumulatorType.OVERFLOW.getName());
            overflow.setValue(overflow.getValue() + (game.getCons() - res.getValue()));
            NotificationUtils.notifyMessage("У вас убыток в размере: " + (game.getCons() - res.getValue()), 10_000);
            accumulatorRepository.save(overflow);
        }
        accumulatorRepository.save(res);
    }
}
