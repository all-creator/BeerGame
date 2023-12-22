package fu.game.beergame.model;

import fu.game.beergame.common.TypeOfPlayer;
import fu.game.beergame.model.modeling.Accumulator;
import fu.game.beergame.model.modeling.GameStream;
import fu.game.beergame.model.modeling.StreamItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    private Session session;

    private TypeOfPlayer currentPlayer = TypeOfPlayer.SELLER;

    private int cons;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Accumulator> accumulators = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<GameStream> streams = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<StreamItem> items = new HashSet<>();

    public Game(Session session) {
        this.session = session;
    }

    public void addAccumulator(Accumulator accumulator) {
        accumulators.add(accumulator);
    }

    public Set<Accumulator> getAllAccumulators(String name) {
        return accumulators.stream().filter(a -> a.getName().equals(name)).collect(Collectors.toSet());
    }

    public Player getCurrentPlayerByType() {
        return session.getPlayers().stream().filter(p -> p.getType() == currentPlayer).findFirst().orElseThrow();
    }

    public void nextPlayer() {
        switch (currentPlayer) {
            case SELLER -> currentPlayer = TypeOfPlayer.PROVIDER;
            case PROVIDER -> currentPlayer = TypeOfPlayer.WHOLESALER;
            case WHOLESALER -> currentPlayer = TypeOfPlayer.FABRIC;
            case FABRIC -> currentPlayer = TypeOfPlayer.SELLER;
        }
    }

    public GameStream getStream(String name) {
        return streams.stream().filter(s -> s.getName().equals(name)).findFirst().orElseThrow();
    }

    public void addStreamItem(StreamItem item) {
        items.add(item);
    }

    public void addStream(GameStream stream) {
        streams.add(stream);
    }
}