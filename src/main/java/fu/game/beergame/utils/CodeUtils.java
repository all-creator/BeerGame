package fu.game.beergame.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CodeUtils {

    private final static Set<Integer> cash = new HashSet<>();
    private final static Random R = new Random();

    public static int getCode() {
        int code = R.nextInt(8999999) + 1000000;
        while(cash.contains(code)) {
            code = R.nextInt(8999999) + 1000000;
        }
        cash.add(code);
        return code;
    }


}
