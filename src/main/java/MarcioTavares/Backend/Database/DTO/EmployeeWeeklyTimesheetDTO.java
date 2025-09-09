package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWeeklyTimesheetDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private WeeklyTimesheetDTO weeklyTimesheet;
}