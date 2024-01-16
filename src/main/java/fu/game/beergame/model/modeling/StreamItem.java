package fu.game.beergame.model.modeling;

import fu.game.beergame.model.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "stream_item")
public class StreamItem {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "item")
    Set<Accumulator> accumulators = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="game_id", nullable=false)
    private Game game;

    public StreamItem(String name, Game game) {
        this.name = name;
        this.game = game;
    }

    public StreamItem addAccumulator(Accumulator accumulator) {
        accumulators.add(accumulator);
        return this;
    }

    public Accumulator getAccumulator(String name) {
        return accumulators.stream().filter(a -> a.getName().equals(name)).findFirst().orElseThrow();
    }
}