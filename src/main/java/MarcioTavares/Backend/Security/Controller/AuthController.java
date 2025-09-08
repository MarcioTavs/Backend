package MarcioTavares.Backend.Security.Controller;

import MarcioTavares.Backend.Security.DTO.*;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import MarcioTavares.Backend.Security.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository UserRepository;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminSignUpRequest request) {
        try {
            AuthResponse response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, e.getMessage()));
        }
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            User user = UserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new UserInfoResponse(user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, e.getMessage()));
        }
    }





} 