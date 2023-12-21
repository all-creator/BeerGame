package fu.game.beergame.model;

import fu.game.beergame.common.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "session")
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    public Session(Integer code, Player player) {
        this.code = code;
        this.players = Set.of(player);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "code")
    private Integer code;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    Set<Player> players;

    SessionStatus status = SessionStatus.INITIALIZED;

    @OneToOne(mappedBy = "session")
    private Game game;

    @CreationTimestamp
    LocalDateTime createdAt;

    public void connectPlayer(Player player) {
        player.setSession(this);
        players.add(player);
    }

    public void disconnectPlayer(Player player) {
        player.setSession(null);
        players.remove(player);
    }

    public void close() {
        getPlayers().forEach(this::disconnectPlayer);
        setStatus(SessionStatus.CLOSED);
    }

    public void updatePlayer(Player player) {
        players.remove(players.stream().filter(p -> p.getUsername().equals(player.getUsername())).findFirst().orElse(null));
        players.add(player);
    }
}