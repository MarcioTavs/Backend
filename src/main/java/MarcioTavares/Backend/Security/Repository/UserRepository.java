package MarcioTavares.Backend.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import MarcioTavares.Backend.Security.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  Optional<User> findByApikey(String apikey);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}

