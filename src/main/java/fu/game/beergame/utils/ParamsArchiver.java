package fu.game.beergame.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ParamsArchiver {

    private static final Gson gson = new GsonBuilder().create();

    public static Parameters getParams(String json) {
        return gson.fromJson(json, Parameters.class);
    }

    public record Parameters(Map<String, Object> params) {}
}
