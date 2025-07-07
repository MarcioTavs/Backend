package MarcioTavares.Backend.Database.Controller;


import MarcioTavares.Backend.Database.DTO.AdminSignUpRequest;
import MarcioTavares.Backend.Database.Model.Admin;
import MarcioTavares.Backend.Database.Repository.AdminRepository;
import MarcioTavares.Backend.Security.Model.Role;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entry")
@AllArgsConstructor
public class EntryController {
       
        private final AdminRepository adminRepository;
        private final UserRepository userRepository;


        @PostMapping("/signup/admin")
        public ResponseEntity<?> adminSignup(@RequestBody AdminSignUpRequest adm){
            try{
                Admin admin = new Admin();
                admin.setFirstName(adm.getFirstName());
                admin.setLastName(adm.getLastName());
                admin.setEmail(adm.getEmail());
                admin.setAdminId(adm.getAdminId());
                admin.setOrganizationName(adm.getOrganizationName());
                admin.setPhoneNumber(adm.getPhoneNumber());

                boolean User = userRepository.existsByEmail(adm.getEmail());
                if(User){
                    throw new RuntimeException("Email already exists");
                }

                User adminUser = new User();
                adminUser.setEmail(adm.getEmail());
                try {
                    if (adm.getPassword() != null && adm.getConfirmPassword() != null && adm.getConfirmPassword().equals(adm.getPassword())) {
                    }
                    adminUser.setPassword(adm.getPassword());
                }catch (Exception e){
                    throw new RuntimeException("Invalid password");
                }
                adminUser.setActive(true);
                adminUser.setRole(Role.ADMIN);
                adminUser.setUsername(adm.getAdminId());
                adminUser.setAdmin(admin);

                adminRepository.save(admin);
                userRepository.save(adminUser);

                return ResponseEntity.ok().body(admin);

            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
}
