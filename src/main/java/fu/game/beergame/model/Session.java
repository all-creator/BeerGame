package fu.game.beergame.model;

import fu.game.beergame.common.SessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
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
}