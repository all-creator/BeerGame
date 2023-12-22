package fu.game.beergame.repository;

import fu.game.beergame.model.modeling.GameStream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStreamRepository extends JpaRepository<GameStream, String> {


}