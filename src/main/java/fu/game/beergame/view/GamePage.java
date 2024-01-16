package fu.game.beergame.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
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
import fu.game.beergame.model.Game;
import fu.game.beergame.model.Player;
import fu.game.beergame.model.modeling.Accumulator;
import fu.game.beergame.model.modeling.GameStream;
import fu.game.beergame.model.modeling.StreamItem;
import fu.game.beergame.repository.AccumulatorRepository;
import fu.game.beergame.repository.GameStreamRepository;
import fu.game.beergame.service.GameService;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.utils.CodeUtils;
import fu.game.beergame.utils.ComponentUtils;
import fu.game.beergame.utils.NotificationUtils;
import fu.game.beergame.view.component.Canvas;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fu.game.beergame.common.AccumulatorType.*;

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
        next.addClickShortcut(Key.ENTER);
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
            cardInput.removeClassName("card_anim_left");
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
            if (!REQUEST.getName().equals(accumulator.getName()))
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
        cardInputText.setText(String.valueOf(game.getPlayerByType(player.getType()).getAccumulator(REQUEST.getName()).getValue()));
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
                case NEW_WEEK -> new GameStream("update", this::calcNewWeek,null, null, game).start();
                case PAGE_RELOAD -> updateHeader();
                case GET_RESOURCE -> {
                    final String[] args = BroadcasterCommand.getFullData(message);
                    if (player.getType().name().equals(args[0]))
                        NotificationUtils.notifySuccess("Вам пришло: "+args[1]+" ресурсов", 10000);
                }
                case GET_REQUEST -> {
                    final String[] args = BroadcasterCommand.getFullData(message);
                    if (player.getType().name().equals(args[0]))
                        NotificationUtils.notifyMessage("Вам пришёл запрос: "+args[1]+" на ресурсов", 10000);
                }
                case SET_REQUEST -> {
                    final String[] args = BroadcasterCommand.getFullData(message);
                    if (player.getType().name().equals(args[0])) {
                        cardInputText.setText(args[1]);
                        cardInput.addClassName("card_anim_left");
                    }
                }
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
            Broadcaster.broadcast(BroadcasterCommand.GET_REQUEST.getCommand() + TypeOfPlayer.getNext(player.getType()) + ":=:" + cardOutputField.getValue().intValue());
            game.getPlayerByType(player.getType()).getAccumulator(REQUEST.getName()).accumulation(cardOutputField.getValue().intValue());
            accumulatorRepository.save(game.getPlayerByType(player.getType()).getAccumulator(REQUEST.getName()));
            game.nextPlayer();
            gameService.save(game);
            if (game.getCurrentPlayer() == TypeOfPlayer.SELLER) game.getStream("newWeek").setStream(() ->
                    game.getAllAccumulators("Week").forEach(accumulator -> {
                        accumulator.setValue(accumulator.getValue() + 1);
                        accumulatorRepository.save(accumulator);
            })).start();
        }).start();
    }

    private void calcNewWeek() {
        log.info("Calculating new week");
        final long start = System.currentTimeMillis();
        Game innGame = gameService.getFetchItems(game);
        if (innGame.getLastCalcWeek() == innGame.getCurrentPlayerByType().getAccumulator(WEEK.getName()).getValue()) return;
        else innGame.setLastCalcWeek(innGame.getCurrentPlayerByType().getAccumulator(WEEK.getName()).getValue());
        List<Player> players = new ArrayList<>();
        players.add(innGame.getPlayerByType(TypeOfPlayer.FABRIC));
        players.add(innGame.getPlayerByType(TypeOfPlayer.WHOLESALER));
        players.add(innGame.getPlayerByType(TypeOfPlayer.PROVIDER));
        players.add(innGame.getPlayerByType(TypeOfPlayer.SELLER));
        log.info("Game fetched in: {} ms", (System.currentTimeMillis() - start));

        players.forEach(p -> {
            final long startInner = System.currentTimeMillis();
            final Accumulator resource = p.getAccumulator(RESOURCE.getName());
            final Accumulator in1 = innGame.getItem(p.getType() + "1").getAccumulator(RESOURCE.getName());
            final Accumulator in2 = innGame.getItem(p.getType() + "2").getAccumulator(RESOURCE.getName());
            final Accumulator request = p.getAccumulator(REQUEST.getName());
            final Accumulator previous = innGame.getPlayerByType(TypeOfPlayer.getPrevious(p.getType())).getAccumulator(REQUEST.getName());
            final Accumulator in2Previous = innGame.getItem(TypeOfPlayer.getPrevious(p.getType()) + "2").getAccumulator(REQUEST.getName());
            final Accumulator in2Request = innGame.getItem(p.getType() + "2").getAccumulator(REQUEST.getName());
            final Accumulator overflow = p.getAccumulator(OVERFLOW.getName());

            log.info("Accumulators fetched in: {} ms", (System.currentTimeMillis() - startInner));
            long startInner2 = System.currentTimeMillis();

            resource.accumulation(in1.getValue());
            Broadcaster.broadcast(BroadcasterCommand.GET_RESOURCE.getCommand() + p.getType() + ":=:" + in1.getValue());
            in1.empty();

            in1.accumulation(in2.getValue());
            in2.empty();

            if (p.getType() != TypeOfPlayer.FABRIC) {
                in2.accumulation(in2Request.getValue());
                in2Request.empty();
            }

            if (p.getType() == TypeOfPlayer.FABRIC) {
                in2.accumulation(request.getValue());
                request.empty();
            }
            if (p.getType() == TypeOfPlayer.SELLER) {
                Broadcaster.broadcast(BroadcasterCommand.SET_REQUEST.getCommand() + p.getType() + ":=:" + (long) innGame.getCons());
                if (resource.getValue() - innGame.getCons() >= 0) {
                    resource.setValue(resource.getValue() - innGame.getCons());
                    overflow.accumulation(resource.getValue());
                    innGame.setCons(0);
                } else {
                    innGame.setCons((int) (innGame.getCons() - resource.getValue()));
                    overflow.accumulation(innGame.getCons() * 2.0);
                    resource.setValue(0);
                }
            } else {
                Broadcaster.broadcast(BroadcasterCommand.SET_REQUEST.getCommand() + p.getType() + ":=:" + (long) previous.getValue());
                if (resource.getValue() >= previous.getValue()) {
                    in2Previous.setValue(previous.getValue());
                    resource.setValue(resource.getValue() - previous.getValue());
                    overflow.accumulation(resource.getValue());
                    previous.setValue(0);
                } else {
                    in2Previous.setValue(resource.getValue());
                    previous.setValue(previous.getValue() - resource.getValue());
                    overflow.accumulation(previous.getValue() * 2);
                    resource.setValue(0);
                    accumulatorRepository.save(overflow);
                }
                accumulatorRepository.save(previous);
            }

            log.info("Accumulators calculated in: {} ms", (System.currentTimeMillis() - startInner2));
            startInner2 = System.currentTimeMillis();

            accumulatorRepository.saveAll(List.of(resource, in1, in2, request, in2Previous, in2Request, overflow));

            log.info("Accumulators saved in: {} ms", (System.currentTimeMillis() - startInner2));
            log.info("Accumulators logic run in: {} ms for player: {}", (System.currentTimeMillis() - startInner), p.getType());
        });
        final int c = CodeUtils.R.nextInt(20);
        log.info("Cons: {}", c);
        innGame.setCons(innGame.getCons() + c);
        gameService.save(innGame);
        log.info("New week calculated in: {} ms", (System.currentTimeMillis() - start));
    }
}
