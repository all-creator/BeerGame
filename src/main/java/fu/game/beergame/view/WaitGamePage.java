package fu.game.beergame.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fu.game.beergame.view.component.Header;
import lombok.RequiredArgsConstructor;

@Route(value = "wait", layout = Header.class)
@AnonymousAllowed
@PageTitle("Wait Game | Beer Game")
@RequiredArgsConstructor
public class WaitGamePage extends VerticalLayout implements HasUrlParameter<String> {

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        add(new H1("Wait Game: " + parameter));
    }
}
