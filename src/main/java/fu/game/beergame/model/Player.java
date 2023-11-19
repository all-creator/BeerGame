package fu.game.beergame.model;

import fu.game.beergame.common.TypeOfPlayer;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private Session session;

    private String username;

    @Transient
    private boolean isReady;

    @Enumerated(EnumType.STRING)
    private TypeOfPlayer type;
}