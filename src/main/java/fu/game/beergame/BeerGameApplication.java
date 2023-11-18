package fu.game.beergame;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("main")
public class BeerGameApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(BeerGameApplication.class, args);
    }

}
