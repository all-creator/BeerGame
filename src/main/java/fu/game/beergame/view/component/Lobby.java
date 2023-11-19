package fu.game.beergame.view.component;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fu.game.beergame.service.PlayerService;
import fu.game.beergame.service.SessionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Lobby extends VerticalLayout {
    protected final transient PlayerService playerService;
    protected final transient SessionService sessionService;
}
