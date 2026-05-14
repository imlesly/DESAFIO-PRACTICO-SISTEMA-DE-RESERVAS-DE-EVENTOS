package sv.edu.udb.eventos_reservas.repository;

import sv.edu.udb.eventos_reservas.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {}