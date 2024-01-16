package fu.game.beergame.repository;

import fu.game.beergame.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("select g from Game g join fetch g.streams where g.id = ?1")
    Game findFetch(Long id);

    @Query("select g from Game g join fetch g.items as i join fetch i.accumulators where g.id = ?1")
    Game findFetchItems(Long id);


}