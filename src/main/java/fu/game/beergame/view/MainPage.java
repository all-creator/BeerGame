package fu.game.beergame.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.exceptions.FieldsValidationError;
import fu.game.beergame.exceptions.GameException;
import fu.game.beergame.exceptions.SessionError;
import fu.game.beergame.model.Player;
import fu.game.beergame.model.Session;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.NotificationUtils;
import fu.game.beergame.view.component.Header;
import fu.game.beergame.view.component.Lobby;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Route(value = "", layout = Header.class)
@AnonymousAllowed
@PageTitle("Start Game | Beer Game")
@Slf4j
public class MainPage extends Lobby {

    // Components
    private final FormLayout formLayout;
    private final TextField username;
    private final TextField code;
    private final Button joinButton;
    private final Button createButton;

    public MainPage(PlayerService playerService, SessionService sessionService) {
        super(playerService, sessionService);

        // Layout setup
        setHeightFull();
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout = new FormLayout(new H1("Beer Game"));
        username = new TextField("Username");
        code = new TextField("Code to join");
        joinButton = new Button("Join to room");
        createButton = new Button("Create new room");
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

        // button setup
        joinButton.addClickListener(this::connectToSession);
        createButton.addClickListener(this::createNewSession);
    }

    private void connectToSession(ClickEvent<Button> event) {
        // Check form fields
        if (code.isEmpty()) throw new FieldsValidationError("Code is not entered");
        if (username.isEmpty()) throw new FieldsValidationError("Username is not entered");
        // Check session
        try {
            final Session session = sessionService.getSession(Integer.parseInt(code.getValue()));
            if (session.getPlayers().size() == 4) throw new SessionError("Session is full");
            switch (session.getStatus()) {
                case INITIALIZED -> throw new SessionError("Session not initialized yet");
                case STARTED -> throw new SessionError("Session already started");
                case FINISHED -> throw new SessionError("Session already finished");
                case READY_TO_CONNECT -> {
                    sessionService.connectToSession(session, new Player(username.getValue()));
                    log.info("Player {} joined to session {}", username.getValue(), session.getId());
                    getUI().ifPresent(ui -> ui.navigate("wait/" + session.getId()));
                    NotificationUtils.notifySuccess("Successfully joined");
                }
            }
        } catch (NumberFormatException ignore) {
            NotificationUtils.notifyError("Code must contain only digits");
        } catch (GameException e) {
            NotificationUtils.notifyError(e.getMessage());
        }
        catch (RuntimeException e) {
            NotificationUtils.notifyError("Not caught error: " + e.getMessage());
        }
    }

    private void createNewSession(ClickEvent<Button> event) {
        if (username.isEmpty()) return;
        final Session session = sessionService.createSession(new Player(username.getValue()));
        getUI().ifPresent(ui -> ui.navigate("create/" + session.getId()));
    }
}
