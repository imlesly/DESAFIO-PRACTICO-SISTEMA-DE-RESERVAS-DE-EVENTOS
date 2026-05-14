package sv.edu.udb.eventos_reservas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.eventos_reservas.dto.AuthRequest;
import sv.edu.udb.eventos_reservas.dto.AuthResponse;
import sv.edu.udb.eventos_reservas.dto.RegisterRequest;
import sv.edu.udb.eventos_reservas.model.User;
import sv.edu.udb.eventos_reservas.repository.UserRepository;
import sv.edu.udb.eventos_reservas.service.JwtService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setAge(request.getAge());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login y obtener JWT")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = (User) auth.getPrincipal();
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user)));
    }
}
