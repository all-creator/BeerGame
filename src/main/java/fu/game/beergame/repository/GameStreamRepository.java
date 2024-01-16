package fu.game.beergame.repository;

import fu.game.beergame.model.modeling.GameStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameStreamRepository extends JpaRepository<GameStream, String> {
    @Query("select g from GameStream g join fetch g.accumulators where g.name = ?1")
    Optional<GameStream> findByIdFetchAccumulators(String name);


}