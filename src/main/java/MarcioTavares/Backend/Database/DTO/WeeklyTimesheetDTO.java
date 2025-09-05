package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTimesheetDTO {
    private Map<String, DailyTimesheetDTO> days = new HashMap<>();
    private BigDecimal totalWorkedHours = BigDecimal.ZERO;
    private Integer totalBreakMinutes = 0;
}
