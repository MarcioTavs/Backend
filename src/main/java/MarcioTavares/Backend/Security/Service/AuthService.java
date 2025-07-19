package MarcioTavares.Backend.Security.Service;

import MarcioTavares.Backend.Database.Model.Admin;
import MarcioTavares.Backend.Database.Repository.AdminRepository;
import MarcioTavares.Backend.Security.DTO.*;
import MarcioTavares.Backend.Security.Model.Role;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Repository.UserRepository;
import MarcioTavares.Backend.Security.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse registerAdmin(AdminSignUpRequest request) {

        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password and confirm password must match");
        }

        Admin admin = new Admin();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setUsername(request.getUsername());
        admin.setOrganizationName(request.getOrganizationName());
        admin.setPhoneNumber(request.getPhoneNumber());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setAdmin(admin);

        adminRepository.save(admin);
        userRepository.save(user);

        String token = jwtUtil.generateToken(createUserDetails(user));

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), "Admin registered successfully");
    }



    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        if (!user.isActive()) {
            throw new RuntimeException("Account is not active");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), "Login successful");
    }


    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

}

