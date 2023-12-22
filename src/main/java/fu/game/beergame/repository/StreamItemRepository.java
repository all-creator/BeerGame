package fu.game.beergame.repository;

import fu.game.beergame.model.modeling.StreamItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamItemRepository extends JpaRepository<StreamItem, Long> {
}