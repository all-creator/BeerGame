package fu.game.beergame.utils;

import com.vaadin.flow.component.Component;
import fu.game.beergame.exceptions.ComponentException;

public class ComponentUtils {

    private ComponentUtils() {}

    public static Component getComponent(Component findIn, String... idsPath) {
        for (String id : idsPath) findIn = findIn.getChildren().filter(e -> e.getId().isPresent()).filter(c -> c.getId().get()
                .equals(id)).findFirst().orElseThrow(new ComponentException("Component with id " + id + " not found"));
        return findIn;
    }
}
