package fu.game.beergame.model;

import fu.game.beergame.common.TypeOfPlayer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "players")
@AllArgsConstructor
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private TypeOfPlayer type;
}