package MarcioTavares.Backend.Database.Service;

import MarcioTavares.Backend.Database.Model.Department;

import MarcioTavares.Backend.Database.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public void createDepartment(Department department) {
        if(departmentRepository.findByName(department.getName()) != null) {
            throw new IllegalArgumentException("Department already exists");
        }
        departmentRepository.save(department);
    }


    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department findByDepartmentId(String departmentId) {

        if(departmentRepository.findByDepartmentId(departmentId) == null) {
            throw new IllegalArgumentException("Department not found");
        }
        return departmentRepository.findByDepartmentId(departmentId);
    }



}

