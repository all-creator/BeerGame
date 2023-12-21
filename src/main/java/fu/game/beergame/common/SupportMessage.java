package fu.game.beergame.common;

import fu.game.beergame.BeerGameApplication;
import fu.game.beergame.utils.CodeUtils;
import lombok.Getter;

@Getter
public enum SupportMessage {
    VERSION("Полная текущая версия игры звучит: BeerGame " + BeerGameApplication.VERSION + " " + BeerGameApplication.RELEASE),
    DESKTOP("Удобней играть на компьютере"),
    TOOLTIPS("При наведении мышки на различные элементы в игре, будут всплывать подсказки"),
    TOOLTIPS_OFF("Всплывающие подсказки можно отключить"),
    CODE("Поделись кодом с другими игроками, что бы они смогли подключиться к тебе"),
    GIT("Если вам интересно как это сделано, смотрите в левый верхний угол"),
    ;

    final String text;

    SupportMessage(String text) {
        this.text = text;
    }

    public static SupportMessage getRandom() {
        return SupportMessage.values()[CodeUtils.R.nextInt(SupportMessage.values().length)];
    }
}
