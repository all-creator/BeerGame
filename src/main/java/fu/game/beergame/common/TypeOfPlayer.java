package fu.game.beergame.common;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public enum TypeOfPlayer {
    SELLER(getSI(), "Магазин", """
            Вы - Магазин лучшая позиция по мнению одних, худшая по мнению других,
            но для вас это не имеет смысла, вы поставили себе цель удовлетворить все потребности всех ваших покупателей и будете идти к ней с уверенностью в глазах.
            Вам на встречу смотрит сама судьба - именно вы начало всей цепочки, вам будут выпадать карточки с определённым кол-вом спроса,
            вам нужно будет запускать цепочку поставок на своё усмотрение, не забывайте про эффект хлыста и наслаждайтесь игрой!"""),
    PROVIDER(getPI(), "Оптовый поставщик", """
            Вы - Оптовый поставщик, который предлагает высокую цену на свою продукцию, вы контролируете этот рынок, вы определяете получат ли магазины то что им так нужно - товар.
            Манипулируйте рынком, создавайте запасы , продавайте их и контролируйте рынок, вы экономический игрок, рынок зависит от вас.
            Доставляете, храните и заказывайте товары у дистрибьютора.
            """),
    WHOLESALER(getWI(), "Дистрибьютор", """
            Ваша цель - довезти, довезти всё что продаётся, не продаться, вообще всё, вы дистрибьютор, пока завод восхваляется своей независимостью и свободой,
             вы знаете точно знаете, самый важный игрок - вы, именно от вас зависит получат ли поставщики товары, а завод прибыль, вы артерия этой игры.
             Доставляете, храните и заказывайте товары у завода.
            """),
    FABRIC(getFI(), "Завод", """
            Вы - Завод, который обеспечивает высокую производительность, вы самая независимая единица в игре, вы сами по себе.
            В отличии от остальных вы владелец своей судьбы, заказывайте, продавайте и контролируйте этот рынок.
            У вас потенциально безграничный запас товаров, ориентируйтесь на других чтобы контролировать свои избытки и удовлетворять потребности дистрибьютора.
            """)
    ;

    public final Icon icon;
    public final String displayName;
    public final String description;

    TypeOfPlayer(Icon icon, String displayName, String description) {
        this.icon = icon;
        this.displayName = displayName;
        this.description = description;
    }

    private static Icon getSI() {
        var icon = VaadinIcon.SHOP.create();
        icon.setColor("red");
        return icon;
    }
    private static Icon getPI() {
        var icon = VaadinIcon.PACKAGE.create();
        icon.setColor("green");
        return icon;
    }
    private static Icon getWI() {
        var icon = VaadinIcon.TRUCK.create();
        icon.setColor("blue");
        return icon;
    }
    private static Icon getFI() {
        var icon = VaadinIcon.FACTORY.create();
        icon.setColor("orange");
        return icon;
    }
}
