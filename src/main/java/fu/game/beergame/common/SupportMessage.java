package fu.game.beergame.common;

import fu.game.beergame.utils.CodeUtils;
import lombok.Getter;

@Getter
public enum SupportMessage {
    VERSION("Полная текущая версия игры звучит: BeerGame v0.0.54-alpha Lobby in Alpha"),
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
        return SupportMessage.values()[CodeUtils.R.nextInt(SupportMessage.values().length-1)];
    }
}
