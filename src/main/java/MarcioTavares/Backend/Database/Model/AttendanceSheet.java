package MarcioTavares.Backend.Database.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private LocalDate date;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private LocalDateTime breakStartTime;
    private LocalDateTime breakEndTime;
    private Integer breakInMinutes;
    private BigDecimal totalHours;


    @ManyToOne(fetch = FetchType.EAGER)
    private Employee employee;


    public Integer getBreakInMinutes() {

        return breakInMinutes != null ? breakInMinutes : 0;
    }

    public BigDecimal calculateTotalHours() {
        if (clockInTime == null) {
            return null;
        }
        long totalMinutes = Duration.between(clockInTime,clockOutTime).toMinutes() - getBreakInMinutes();
        return BigDecimal.valueOf(totalMinutes).
                divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    public void calculateAndSetTotalHours() {
        if (clockOutTime != null) {
            this.totalHours = calculateTotalHours();

        }
    }



}
