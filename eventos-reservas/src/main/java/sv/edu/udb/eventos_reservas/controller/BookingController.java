package sv.edu.udb.eventos_reservas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.eventos_reservas.dto.BookingRequest;
import sv.edu.udb.eventos_reservas.model.Booking;
import sv.edu.udb.eventos_reservas.model.Event;
import sv.edu.udb.eventos_reservas.model.User;
import sv.edu.udb.eventos_reservas.repository.BookingRepository;
import sv.edu.udb.eventos_reservas.repository.EventRepository;
import sv.edu.udb.eventos_reservas.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Reservas")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Crear reserva")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Validar disponibilidad
        List<Booking> existing = bookingRepository.findAll().stream()
                .filter(b -> b.getEvent().getIdEvent().equals(event.getIdEvent())
                        && b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .toList();
        int totalReserved = existing.stream().mapToInt(Booking::getQuantity).sum();
        if (totalReserved + request.getQuantity() > event.getCapacity()) {
            return ResponseEntity.badRequest()
                    .body("No hay suficientes cupos disponibles. Disponibles: "
                            + (event.getCapacity() - totalReserved));
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(user);
        booking.setQuantity(request.getQuantity());
        booking.setTotalAmount(event.getPricePerTicket()
                .multiply(BigDecimal.valueOf(request.getQuantity())));
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        return ResponseEntity.ok(bookingRepository.save(booking));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Mis reservas")
    public ResponseEntity<List<Booking>> myBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(bookingRepository.findByUser(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<Booking> cancel(@PathVariable Integer id) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus(Booking.BookingStatus.CANCELLED);
            return ResponseEntity.ok(bookingRepository.save(b));
        }).orElse(ResponseEntity.notFound().build());
    }
}