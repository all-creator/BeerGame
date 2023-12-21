package fu.game.beergame;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@Theme("main")
@Push(PushMode.AUTOMATIC)
public class BeerGameApplication implements AppShellConfigurator {

    public static final String VERSION = "0.0.57-alpha";
    public static final String RELEASE = "Lobby alpha";


    public static void main(String[] args) {
        SpringApplication.run(BeerGameApplication.class, args);
    }

}
