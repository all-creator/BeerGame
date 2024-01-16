package fu.game.beergame.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BroadcasterCommand {
    SUPPORT_MESSAGE_UPDATE("$update_support"),
    CONSOLE_MESSAGE("$console_message@:"),
    PLAYER_READY("$player_ready@:"),
    PLAYER_LEFT("$player_left@:"),
    PLAYER_JOINED("$player_joined@:"),
    START_GAME("$start_game"),
    NEXT_TURN("$next_turn"),
    PAGE_RELOAD("$page_reload"),
    NEW_WEEK("$new_week"),
    GET_RESOURCE("$get_resource@:"),
    SET_REQUEST("$set_request@:"),
    GET_REQUEST("$get_request@:"),
    ;
    final String command;

    BroadcasterCommand(String command) {
        this.command = command;
    }

    public static boolean isCommand(String msg) {
        return msg.startsWith("$");
    }

    public static BroadcasterCommand getCommand(String msg) {
        return Arrays.stream(BroadcasterCommand.values()).filter(b -> msg.startsWith(b.command)).findFirst().orElseThrow();
    }

    public static String getData(String msg) {
        return msg.split("@:")[1];
    }

    public static String[] getFullData(String msg) {
        return msg.split("@:")[1].split(":=:");
    }

    public static boolean hasData(String msg) {
        return msg.contains("@:");
    }
}
