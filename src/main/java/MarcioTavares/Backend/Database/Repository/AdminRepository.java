package MarcioTavares.Backend.Database.Repository;

import MarcioTavares.Backend.Database.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByAdminId(String adminId);
}
