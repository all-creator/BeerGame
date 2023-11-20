package fu.game.beergame.view.component;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Header extends AppLayout {
    public Header() {
        var version = new Span("v0.0.53-alpha");
        version.getStyle().set("color", "#999");
        version.getStyle().set("font-size", "10px");
        version.getStyle().set("margin", "1px");
        var git = new Span("https://github.com/all-creator/BeerGame");
        git.getStyle().set("color", "#999");
        git.getStyle().set("font-size", "10px");
        git.getStyle().set("margin", "1px");
        HorizontalLayout head = new HorizontalLayout(git, version);
        head.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        head.setHeight("56px");
        head.setWidthFull();
        head.addClassName("header");
        addToNavbar(head);
    }
}
