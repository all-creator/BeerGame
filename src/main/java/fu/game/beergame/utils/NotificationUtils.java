package fu.game.beergame.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationUtils {
    private NotificationUtils() {}

    public static void notifyError(String message) {
        var n = Notification.show(message);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static void notifySuccess(String message) {
        var n = Notification.show(message);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void notifyMessage(String message, int time) {
        var n = Notification.show(message);
        n.setDuration(time);
        n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
