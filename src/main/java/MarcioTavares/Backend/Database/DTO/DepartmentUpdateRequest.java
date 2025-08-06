package MarcioTavares.Backend.Database.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentUpdateRequest {
    private String departmentId;
    private String departmentName;

}
