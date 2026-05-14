package sv.edu.udb.eventos_reservas.repository;

import sv.edu.udb.eventos_reservas.model.Booking;
import sv.edu.udb.eventos_reservas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser(User user);
}