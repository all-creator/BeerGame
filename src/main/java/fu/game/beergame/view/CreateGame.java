package fu.game.beergame.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.common.SessionStatus;
import fu.game.beergame.exceptions.FieldsValidationError;
import fu.game.beergame.exceptions.GameException;
import fu.game.beergame.model.Player;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.utils.NotificationUtils;
import fu.game.beergame.view.component.GameLobby;
import fu.game.beergame.view.component.Header;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AnonymousAllowed
@PageTitle("Start Game | Beer Game")
@Route(value = "create", layout = Header.class)
public class CreateGame extends GameLobby {
    // Component's
    private VerticalLayout layout;
    private Dialog dialog;
    private FormLayout formLayout;
    private Button createButton;

    protected CreateGame(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);
    }

    @Override
    protected void addUI() {
        // Add UI
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout = new FormLayout(new H1("Create Game"), new Span("Your game code is: "+session.getCode()));
        createButton = new Button("Create game", this::startGame);
        formLayout.setColspan(createButton, 2);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        formLayout.setColspan(sel, 2);
        createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        formLayout.add(sel, createButton);
        setAlignSelf(Alignment.CENTER, formLayout);
        add(formLayout);
    }

    public void startGame(ClickEvent<Button> event) {
        try {
            // Fields validation
            if (sel.isEmpty()) throw new FieldsValidationError("Select player type");
            // Open Session
            sessionService.openSession(session, player);
            player.setType(sel.getValue());
            playerService.save(player);
            // Add and configure UI
            layout = new VerticalLayout();
            dialog = new Dialog();

            dialog.setMaxHeight("70%");
            dialog.setMaxWidth("70%");
            dialog.setMinHeight("50%");
            dialog.setMinWidth("50%");

            dialog.setHeaderTitle(player.getType().name());
            dialog.getHeader().add(player.getType().icon);
            dialog.getHeader().add(new Button(VaadinIcon.CLOSE.create(), this::closeGame));

            layout.setMargin(true);
            layout.add(new H4("Game code is: " + session.getCode()));
            layout.add(new Span("Описание (" + player.getType().displayName + "):"));
            layout.add(new Span(player.getType().description));

            layout.add(new Span("Ожидание игроков:"));
            layout.add(new Span(session.getPlayers().size() + "/4"));

            ProgressBar progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);

            dialog.add(layout);
            dialog.add(progressBar);
            dialog.open();

            dialog.setCloseOnOutsideClick(false);

            // Register Broadcaster
            broadcasterRegistration = Broadcaster.register(newMessage -> getUI().ifPresent(ui -> ui.access(() -> {
                log.info("New Broadcaster message: {}", newMessage);
                layout.getChildren().filter(Span.class::isInstance).filter(e -> ((Span) e).getText().endsWith("/4")).findFirst().ifPresent(e -> ((Span) e).setText(sessionService.getSession(gameUuid).getPlayers().size() + "/4"));
            })));
        } catch (GameException e) {
            NotificationUtils.notifyError(e.getMessage());
        }
    }

    public void closeGame(ClickEvent<Button> event) {
        sessionService.closeSession(session);
        dialog.close();
    }
}
