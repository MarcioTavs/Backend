package MarcioTavares.Backend.Database.Repository;

import MarcioTavares.Backend.Database.Model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByDepartmentId(String departmentId);
    Department findByName(String name);
}
