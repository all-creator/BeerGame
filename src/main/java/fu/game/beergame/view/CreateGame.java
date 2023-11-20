package fu.game.beergame.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.common.BroadcasterCommand;
import fu.game.beergame.common.SupportMessage;
import fu.game.beergame.common.TypeOfPlayer;
import fu.game.beergame.exceptions.FieldsValidationError;
import fu.game.beergame.exceptions.GameException;
import fu.game.beergame.model.Player;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.utils.ComponentUtils;
import fu.game.beergame.utils.NotificationUtils;
import fu.game.beergame.view.component.GameLobby;
import fu.game.beergame.view.component.Header;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Getter
@AnonymousAllowed
@PageTitle("Start Game | Beer Game")
@Route(value = "create", layout = Header.class)
public class CreateGame extends GameLobby {
    // Component's
    private VerticalLayout layout;
    private final Select<TypeOfPlayer> sel = new Select<>();
    private Dialog dialog;
    private FormLayout formLayout;
    private Button createButton;
    private Scroller scroller;
    private VerticalLayout scrollerLayout;

    protected CreateGame(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);
    }

    @Override
    protected void addUI() {
        // Add UI
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout = new FormLayout(new H1("Create Game"), new Span("Your lobby code is: "+session.getCode()));
        createButton = new Button("Create game", this::openConfigGameModal);
        formLayout.setColspan(createButton, 2);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        formLayout.setColspan(sel, 2);
        createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        formLayout.add(sel, createButton);
        setAlignSelf(Alignment.CENTER, formLayout);
        add(formLayout);
    }

    @Override
    protected Select<TypeOfPlayer> getSelect() {
        return sel;
    }

    public void openConfigGameModal(ClickEvent<Button> event) {
        try {
            // Fields validation
            if (sel.isEmpty()) throw new FieldsValidationError("Select player type");
            // Open Session
            player.setType(sel.getValue());
            sessionService.openSession(session, player);
            // Add and configure UI
            layout = new VerticalLayout();
            layout.setMargin(true);

            dialog = new Dialog();
            dialog.setMaxHeight("80%");
            dialog.setMaxWidth("75%");
            dialog.setMinHeight("50%");
            dialog.setMinWidth("50%");
            dialog.setCloseOnOutsideClick(false);

            scrollerLayout = new VerticalLayout();
            scrollerLayout.setWidthFull();
            scrollerLayout.setHeightFull();
            scrollerLayout.setMargin(false);
            scrollerLayout.setPadding(false);

            scroller = new Scroller(scrollerLayout);
            scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
            scroller.setWidthFull();
            scroller.setMaxHeight("100px");

            ProgressBar progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);

            final HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setWidthFull();
            buttonLayout.setMargin(true);

            final HorizontalLayout infoLayout = new HorizontalLayout();
            buttonLayout.setWidthFull();
            buttonLayout.setMargin(true);
            infoLayout.setId("info-layout");

            final Span playerCount = new Span("Игроков в игре: " + session.getPlayers().size() + "/4");
            playerCount.setId("player-count");

            final Span playerReadyCount = new Span("Ожидание игроков: " + session.getPlayers().stream().filter(Player::isReady).count() + "/4");
            playerReadyCount.setId("player-ready-count");

            final Span supportMessage = new Span("Подсказка: " + SupportMessage.getRandom().getText());
            supportMessage.setId("support-message");
            supportMessage.getStyle().set("font-size", "11px");

            final Button startButton = new Button("Start Game", this::startGame);
            startButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            startButton.setWidth("48%");
            startButton.setEnabled(false);

            final Button readyButton = new Button("Ready", this::playerReady);
            readyButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            readyButton.setWidth("48%");
            readyButton.setDisableOnClick(true);

            dialog.setHeaderTitle(player.getType().name());
            dialog.getHeader().add(player.getType().create(), new Button(VaadinIcon.CLOSE.create(), this::closeGame));

            scrollerLayout.add(new Span("Player " + player.getUsername() + " init game session: " + session.getId()));

            infoLayout.add(playerCount, playerReadyCount);

            layout.add(new H4("Lobby code is: " + session.getCode()));
            layout.add(new Span("Описание (" + player.getType().displayName + "):"));
            layout.add(new Span(player.getType().description));
            layout.add(infoLayout, new H5("Lobby Log's:"), scroller);

            buttonLayout.add(readyButton, startButton);

            dialog.add(layout, progressBar, supportMessage, buttonLayout);
            dialog.open();

            // Register Broadcaster
            broadcasterRegistration = Broadcaster.register(newMessage -> getUI().ifPresent(ui -> ui.access(() -> {
                if (BroadcasterCommand.isCommand(newMessage)) {
                    switch (BroadcasterCommand.getCommand(newMessage)) {
                        case SUPPORT_MESSAGE_UPDATE -> ((Span) ComponentUtils.getComponent(dialog, "support-message"))
                                .setText("Подсказка: " + SupportMessage.getRandom().getText());
                    }
                } else {
                    session = sessionService.getSession(gameUuid);
                    log.info("New Broadcaster message: {}, for session: {}", newMessage, session);
                    ((Span) getComponentFromInfo("player-count")).setText("Игроков в игре: " + session.getPlayers().size() + "/4");
                    ((Span) getComponentFromInfo("player-ready-count")).setText("Ожидание игроков: " + session.getPlayers().stream().filter(Player::isReady).count() + "/4");
                    scrollerLayout.add(new Span(newMessage));
                    if (session.getPlayers().stream().filter(Player::isReady).count() == 4)
                        startButton.setEnabled(true);
                    if (session.getPlayers().stream().filter(Player::isReady).count() < 4)
                        startButton.setEnabled(false);
                }
            })));
        } catch (GameException e) {
            NotificationUtils.notifyError(e.getMessage());
        }
    }

    public void closeGame(ClickEvent<Button> event) {
        sessionService.closeSession(session);
        dialog.close();
    }

    public void startGame(ClickEvent<Button> event) {
        NotificationUtils.notifyError("Soon!");
    }

    public void playerReady(ClickEvent<Button> event) {
        playerService.ready(player, session);
    }

    @Scheduled(fixedRate = 10000L)
    public void supportUpdate() {
        if (dialog != null) Broadcaster.broadcast(BroadcasterCommand.SUPPORT_MESSAGE_UPDATE);
    }

    private Component getComponentFromInfo(String id) {
        return ComponentUtils.getComponent(layout, "info-layout", id);
    }

}
