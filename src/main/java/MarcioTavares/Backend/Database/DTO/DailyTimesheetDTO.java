package MarcioTavares.Backend.Database.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data

public class DailyTimesheetDTO {

    private BigDecimal workedHours;
    private Integer breakMinutes;

    public DailyTimesheetDTO() {
        this.workedHours = BigDecimal.ZERO;
        this.breakMinutes = 0;
    }

    public DailyTimesheetDTO(BigDecimal workedHours, Integer breakMinutes) {
        this.workedHours = workedHours;
        this.breakMinutes = breakMinutes;
    }
}
