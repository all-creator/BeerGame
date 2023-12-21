package fu.game.beergame.view.component;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import fu.game.beergame.model.Game;
import fu.game.beergame.service.GameService;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import fu.game.beergame.utils.ParamsArchiver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class Canvas extends VerticalLayout implements HasUrlParameter<String> {
    protected final transient PlayerService playerService;
    protected final transient SessionService sessionService;
    protected final GameService gameService;
    Game game;

    // Component
    protected Header header = new Header();
    protected SideNav sideNav = new SideNav();
    protected VerticalLayout main = new VerticalLayout();

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        game = sessionService.loadGame(parameter);
        configureUI();
        addUI();
    }

    protected abstract void addUI();
    protected void configureUI(){
        loadHeader();
        loadSideBar();
        loadMainScreen();
    }

    protected void loadHeader(){
        var params = ParamsArchiver.getParams(game.getParams());
        for (Map.Entry<String, Object> entry : params.params().entrySet()) {
            header.add(new H4(entry.getKey() + ": " + entry.getValue()));
        }
    }
    protected void loadSideBar() {

    }
    protected void loadMainScreen() {

    }
}
