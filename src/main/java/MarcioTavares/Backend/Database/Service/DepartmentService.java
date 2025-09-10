package MarcioTavares.Backend.Database.Service;

import MarcioTavares.Backend.Database.DTO.DepartmentDTO;

import MarcioTavares.Backend.Database.Model.Department;

import MarcioTavares.Backend.Database.Repository.DepartmentRepository;
import jakarta.transaction.Transactional;
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



//    @Transactional
//    public Department updateDepartment(DepartmentDTO departUpdate, String departId) {
//        Department department = departmentRepository.findByDepartmentId(departId);
//        if (department == null) {
//            throw new IllegalArgumentException("Department not found");
//        }
//
//        if (departUpdate.getDepartmentId() != null &&
//                !departUpdate.getDepartmentId().equals(departId)) {
//
//            Department existDept = departmentRepository.findByDepartmentId(departUpdate.getDepartmentId());
//            if (existDept != null) {
//                throw new IllegalArgumentException("Department ID already exists");
//            }
//            department.setDepartmentId(departUpdate.getDepartmentId());
//        }
//
//        if (departUpdate.getDepartmentName() != null) {
//            Department existByName = departmentRepository.findByName(departUpdate.getDepartmentName());
//            if (existByName != null && !existByName.getId().equals(department.getId())) {
//                throw new IllegalArgumentException("Department name already exists");
//            }
//
//            department.setName(departUpdate.getDepartmentName());
//
//            department.getEmployees().forEach(employee -> {
//                employee.setDepartmentName(departUpdate.getDepartmentName());
//            });
//        }
//
//        departmentRepository.save(department);
//        return department;
//    }





}

