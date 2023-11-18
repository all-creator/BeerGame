package fu.game.beergame.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.model.Player;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.view.component.Header;
import lombok.extern.slf4j.Slf4j;

@Route(value = "", layout = Header.class)
@AnonymousAllowed
@PageTitle("Start Game | Beer Game")
@Slf4j
public class MainPage extends VerticalLayout {

    private final PlayerService playerService;
    private final SessionService sessionService;


    public MainPage(PlayerService playerService, SessionService sessionService) {
        this.playerService = playerService;
        this.sessionService = sessionService;
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        FormLayout formLayout = new FormLayout(new H1("Beer Game"));
        var username = new TextField("Username");
        var code = new TextField("Code to join");
        var joinButton = new Button("Join to room");
        var createButton = new Button("Create new room");
        username.setRequired(true);
        username.setAutofocus(true);
        joinButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        createButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        formLayout.add(username, code, joinButton, createButton);
        formLayout.setColspan(username, 2);
        formLayout.setColspan(code, 2);
        formLayout.setMaxWidth("500px");
        setAlignSelf(Alignment.CENTER, formLayout);
        add(formLayout);

        joinButton.addClickListener(e -> {
            if (username.isEmpty() || code.isEmpty()) return;
            var session = sessionService.getSession(Integer.parseInt(code.getValue()));
            switch (session.getStatus()) {
                case INITIALIZED -> {
                    var n = Notification.show("Session not initialized yet");
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                case STARTED -> {
                    var n = Notification.show("Session already started");
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                case FINISHED -> {
                    var n = Notification.show("Session already finished");
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                case READY_TO_CONNECT -> {
                    var player = new Player(username.getValue());
                    player.setSession(session);
                    playerService.save(player);
                    session.getPlayers().add(player);
                    log.info("Player {} joined to session {}", player.getUsername(), session.getId());
                    sessionService.save(session);
                    CreateGame.games.get(session.getId().toString()).updatePlayers();
                    getUI().ifPresent(ui -> ui.navigate("wait/" + session.getId()));
                }
            }
        });
        createButton.addClickListener(e -> {
            if (username.isEmpty()) return;
            var session = sessionService.createSession(new Player(username.getValue()));
            getUI().ifPresent(ui -> ui.navigate("create/" + session.getId()));
        });
    }
}
