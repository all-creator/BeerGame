package fu.game.beergame.view.component;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Header extends AppLayout {
    public Header() {
        var s = new Span("v0.0.37");
        s.getStyle().set("color", "#999");
        s.getStyle().set("font-size", "10px");
        HorizontalLayout head = new HorizontalLayout(s);
        head.setHeight("56px");
        head.setWidthFull();
        head.addClassName("header");
        addToNavbar(head);
    }
}
