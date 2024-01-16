package fu.game.beergame.model.modeling;

import fu.game.beergame.model.Game;
import fu.game.beergame.utils.Broadcaster;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "game_stream")
public class GameStream {
    @Id
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    private String eventOnStart;

    private String eventOnEnd;

    @Transient
    private Runnable stream;

    @Getter
    @OneToMany(fetch = FetchType.LAZY)
    Set<Accumulator> accumulators = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="game_id", nullable=false)
    private Game game;

    public GameStream(String name, Runnable stream, String eventOnStart, String eventOnEnd, Game game) {
        this.name = name;
        this.eventOnStart = eventOnStart;
        this.eventOnEnd = eventOnEnd;
        this.stream = stream;
        this.game = game;
    }

    public void start() {
        if (eventOnStart != null) Broadcaster.broadcast(eventOnStart);
        stream.run();
        if (eventOnEnd != null) Broadcaster.broadcast(eventOnEnd);
    }

    public GameStream setStream(Runnable stream) {
        this.stream = stream;
        return this;
    }

    public void addAccumulator(Accumulator accumulator) {
        accumulators.add(accumulator);
    }
}