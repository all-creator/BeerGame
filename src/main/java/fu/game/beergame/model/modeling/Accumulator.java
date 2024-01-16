package fu.game.beergame.model.modeling;

import fu.game.beergame.model.Game;
import fu.game.beergame.model.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "accumulator")
public class Accumulator {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name="item_id")
    private StreamItem item;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name="game_id", nullable=false)
    private Game game;

    private String name;

    private double value;

    public Accumulator(Player player, Game game, String name, double value) {
        this.player = player;
        this.game = game;
        this.name = name;
        this.value = value;
    }

    public Accumulator(StreamItem item, Game game, String name, double value) {
        this.item = item;
        this.game = game;
        this.name = name;
        this.value = value;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    public void accumulation(double value) {
        this.value += value;
    }

    public void empty() {
        this.value = 0;
    }
}