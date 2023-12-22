package fu.game.beergame.repository;

import fu.game.beergame.model.modeling.Accumulator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccumulatorRepository extends JpaRepository<Accumulator, Long> {
}