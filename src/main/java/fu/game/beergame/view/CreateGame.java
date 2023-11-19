package fu.game.beergame.view;

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
import fu.game.beergame.model.Player;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.Broadcaster;
import fu.game.beergame.view.component.GameLobby;
import fu.game.beergame.view.component.Header;
import lombok.extern.slf4j.Slf4j;

@Route(value = "create", layout = Header.class)
@AnonymousAllowed
@PageTitle("Start Game | Beer Game")
@Slf4j
public class CreateGame extends GameLobby {
    private VerticalLayout layout;
    Dialog dialog;

    protected CreateGame(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);
    }

    @Override
    protected void addUI() {
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        FormLayout formLayout = new FormLayout(new H1("Create Game"), new Span("User game code is: "+session.getCode()));
        var createButton = new Button("Create game", e -> startGame());
        formLayout.setColspan(createButton, 2);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        formLayout.setColspan(sel, 2);
        createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        formLayout.add(sel, createButton);
        setAlignSelf(Alignment.CENTER, formLayout);
        add(formLayout);
    }

    public void startGame() {
        session = sessionService.getSession(gameUuid);
        layout = new VerticalLayout();
        dialog = new Dialog();
        Player player = session.getPlayers().iterator().next();
        session.setStatus(SessionStatus.READY_TO_CONNECT);
        player.setType(sel.getValue());
        sessionService.save(session);
        playerService.save(player);

        dialog.setMaxHeight("70%");
        dialog.setMaxWidth("70%");
        dialog.setMinHeight("50%");
        dialog.setMinWidth("50%");

        dialog.setHeaderTitle(player.getType().name());
        dialog.getHeader().add(player.getType().icon);
        dialog.getHeader().add(new Button(VaadinIcon.CLOSE.create(), e -> closeGame()));

        layout.setMargin(true);
        layout.add(new H4("Game code is: " + session.getCode()));
        layout.add(new Span("Описание ("+ player.getType().displayName+"):"));
        layout.add(new Span(player.getType().description));

        layout.add(new Span("Ожидание игроков:"));

        var count = new Span(session.getPlayers().size() + "/4");
        layout.add(count);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);

        dialog.add(layout);
        dialog.add(progressBar);
        dialog.open();

        dialog.setCloseOnOutsideClick(false);

        broadcasterRegistration = Broadcaster.register(newMessage -> getUI().ifPresent(ui -> ui.access(() -> {
            log.info("Update players");
            layout.getChildren().filter(Span.class::isInstance).filter(e -> ((Span) e).getText().endsWith("/4")).findFirst().ifPresent(e -> ((Span) e).setText(sessionService.getSession(gameUuid).getPlayers().size() + "/4"));
        })));
    }

    public void closeGame() {
        session.setStatus(SessionStatus.INITIALIZED);
        sessionService.save(session);

        dialog.close();
    }
}
