package MarcioTavares.Backend.Database.Repository;

import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceSheet , Long> {

    Optional<AttendanceSheet> findByEmployeeAndClockOutTimeIsNull(Employee employee);
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);

    List<AttendanceSheet> findByEmployeeAndDateBetween(Employee employee,LocalDate startDate, LocalDate endDate);
    List<AttendanceSheet> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
