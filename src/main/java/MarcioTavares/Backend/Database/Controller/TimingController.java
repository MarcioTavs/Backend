package MarcioTavares.Backend.Database.Controller;

import MarcioTavares.Backend.Database.DTO.WeeklyTimesheetDTO;
import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Service.TimingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
@AllArgsConstructor
public class TimingController {

    private final TimingService timingService;

    @PostMapping("/clockIn")
    public ResponseEntity<?> clockIn() {
        try {
            AttendanceSheet att = timingService.clockIn();
            return ResponseEntity.ok(att);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/clockOut")
    public ResponseEntity<?> clockOut() {
        try {
            AttendanceSheet att = timingService.clockOut();
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("totalHours", att.getTotalHours());
                put("breakInMinutes", att.getBreakInMinutes());
                put("workTimeInMinutes", att.getWorkTimeInMinutes());
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/startBreak")
    public ResponseEntity<?> startBreak() {
        try {
            AttendanceSheet att = timingService.startBreak();
            return ResponseEntity.ok(att);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/endBreak")
    public ResponseEntity<?> endBreak() {
        try {
            AttendanceSheet att = timingService.endBreak();
            return ResponseEntity.ok(att);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAttendanceStatus() {
        try {
            Employee emp = timingService.getEmployeeService().getCurrentAuthenticatedEmployee();
            Optional<AttendanceSheet> att = timingService.getAttendanceRepository().findByEmployeeAndClockOutTimeIsNull(emp);
            if (att.isPresent()) {
                AttendanceSheet attendance = att.get();
                LocalDateTime now = LocalDateTime.now();
                attendance.calculateAndSetWorkTimeInMinutes(now);
                timingService.getAttendanceRepository().save(attendance);
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("isClockedIn", true);
                    put("isOnBreak", attendance.getBreakStartTime() != null && attendance.getBreakEndTime() == null);
                    put("workTimeInMinutes", attendance.getWorkTimeInMinutes());
                    put("breakInMinutes", attendance.getBreakInMinutes());
                }});
            } else {
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("isClockedIn", false);
                    put("isOnBreak", false);
                    put("workTimeInMinutes", 0);
                    put("breakInMinutes", 0);
                }});
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/weeklyReport")
    public ResponseEntity<?> getWeeklyReport(@RequestParam("startDate") String startDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr); // Expect format like "2025-09-01"
            Employee emp = timingService.getEmployeeService().getCurrentAuthenticatedEmployee();
            WeeklyTimesheetDTO dto = timingService.getWeeklyReport(emp, startDate);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}