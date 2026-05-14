package sv.edu.udb.eventos_reservas.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Integer eventId;
    private Integer quantity;
}
