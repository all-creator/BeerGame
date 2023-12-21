package fu.game.beergame.repository;

import fu.game.beergame.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByCode(Integer code);

    @Query("select s from Session s join fetch Game g where s.id = ?1")
    Optional<Session> findFetchGameById(UUID id);


}