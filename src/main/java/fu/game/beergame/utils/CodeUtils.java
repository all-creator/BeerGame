package fu.game.beergame.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CodeUtils {

    private CodeUtils() {}

    private static final Set<Integer> cash = new HashSet<>();
    public static final Random R = new Random();

    public static int getCode() {
        int code = R.nextInt(899999) + 100000;
        while(cash.contains(code)) {
            code = R.nextInt(899999) + 100000;
        }
        cash.add(code);
        return code;
    }


}
