package fu.game.beergame.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BroadcasterCommand {
    SUPPORT_MESSAGE_UPDATE("$update_support")
    ;
    final String command;

    BroadcasterCommand(String command) {
        this.command = command;
    }

    public static boolean isCommand(String msg) {
        return msg.startsWith("$");
    }

    public static BroadcasterCommand getCommand(String msg) {
        return Arrays.stream(BroadcasterCommand.values()).filter(b -> b.getCommand().equals(msg)).findFirst().orElseThrow();
    }
}
