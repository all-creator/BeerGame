package fu.game.beergame.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccumulatorType {

    RESOURCE("Resource"),
    WEEK("Week"),
    OVERFLOW("Overflow"),
    REQUEST("Request"),
    ;

    final String name;

}
