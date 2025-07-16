package MarcioTavares.Backend.Database.Model;


import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Integer breakInMinutes;
    private BigDecimal totalHours;

    @ManyToOne(fetch = FetchType.EAGER)
    private Employee employee;

    public Integer getBreakMinutes() {
        return breakInMinutes != null ? breakInMinutes : 0;
    }


    public BigDecimal calculateTotalHours() {
        if (clockOutTime == null) {
            return null;
        }

        long totalMinutes = Duration.between(clockInTime, clockOutTime).toMinutes() - getBreakMinutes();
        return BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    public void calculateAndSetTotalHours() {
        if (clockOutTime != null) {
            this.totalHours = calculateTotalHours();
        }
    }



}
