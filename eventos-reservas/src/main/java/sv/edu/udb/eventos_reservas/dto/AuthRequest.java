package sv.edu.udb.eventos_reservas.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}