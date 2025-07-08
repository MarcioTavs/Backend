package MarcioTavares.Backend.Database.DTO;


import lombok.Data;

@Data
public class DepartmentUpdateRequest {
    private String departmentId;
    private String departmentName;

}
