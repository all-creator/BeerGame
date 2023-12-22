package fu.game.beergame.model;

import fu.game.beergame.common.TypeOfPlayer;
import fu.game.beergame.model.modeling.Accumulator;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "players")
public class Player {
    public Player(String username) {
        this.username = username;
    }

    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private Session session;

    private String username;

    private boolean isReady;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    @ToString.Exclude
    Set<Accumulator> accumulators;

    @Enumerated(EnumType.STRING)
    private TypeOfPlayer type;

    public Accumulator getAccumulator(String name) {
        return accumulators.stream().filter(a -> a.getName().equals(name)).findFirst().orElseThrow();
    }
}