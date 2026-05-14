package sv.edu.udb.eventos_reservas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.eventos_reservas.model.Event;
import sv.edu.udb.eventos_reservas.repository.EventRepository;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Eventos")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventRepository eventRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Listar todos los eventos")
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Obtener evento por ID")
    public ResponseEntity<Event> getById(@PathVariable Integer id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Crear evento")
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.ok(eventRepository.save(event));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Actualizar evento")
    public ResponseEntity<Event> update(@PathVariable Integer id, @RequestBody Event event) {
        return eventRepository.findById(id).map(e -> {
            event.setIdEvent(id);
            return ResponseEntity.ok(eventRepository.save(event));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Eliminar evento")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}