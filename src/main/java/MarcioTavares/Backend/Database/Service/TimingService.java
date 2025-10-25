package MarcioTavares.Backend.Database.Service;

import MarcioTavares.Backend.Database.DTO.DailyTimesheetDTO;
import MarcioTavares.Backend.Database.DTO.EmployeeWeeklyTimesheetDTO;
import MarcioTavares.Backend.Database.DTO.WeeklyTimesheetDTO;
import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Repository.AttendanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@Service
@AllArgsConstructor
public class TimingService {

    private final EmployeeService employeeService;
    private final AttendanceRepository attendanceRepository;

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public AttendanceRepository getAttendanceRepository() {
        return attendanceRepository;
    }

    @Transactional
    public AttendanceSheet clockIn() {
        Employee emp = employeeService.getCurrentAuthenticatedEmployee();
        Optional<AttendanceSheet> att = attendanceRepository.findByEmployeeAndClockOutTimeIsNull(emp);
        if (att.isPresent()) {
            throw new IllegalStateException("Sorry You are already logged in");
        }

        LocalDate date = LocalDate.now();
        if (attendanceRepository.existsByEmployeeAndDate(emp, date)) {
            throw new IllegalStateException("You already have a work session for today");
        }
        AttendanceSheet attendanceSheet = new AttendanceSheet();
        attendanceSheet.setEmployee(emp);
        attendanceSheet.setDate(date);
        attendanceSheet.setClockInTime(LocalDateTime.now());
        attendanceSheet.setWorkTimeInMinutes(0);
        System.out.println("Clock-in at: " + attendanceSheet.getClockInTime());
        return attendanceRepository.save(attendanceSheet);
    }

    @Transactional
    public AttendanceSheet startBreak() {
        Employee emp = employeeService.getCurrentAuthenticatedEmployee();
        AttendanceSheet att = attendanceRepository.findByEmployeeAndClockOutTimeIsNull(emp)
                .orElseThrow(() -> new IllegalStateException("No active clock-in session found"));
        if (att.getBreakStartTime() != null && att.getBreakEndTime() == null) {
            throw new IllegalStateException("A break is already in progress");
        }
        LocalDateTime now = LocalDateTime.now();
        att.calculateAndSetWorkTimeInMinutes(now);
        att.setBreakStartTime(now);
        att.setBreakEndTime(null);
        System.out.println("Break started at: " + att.getBreakStartTime());
        return attendanceRepository.save(att);
    }



    @Transactional
    public AttendanceSheet clockOut() {
        Employee emp = employeeService.getCurrentAuthenticatedEmployee();
        AttendanceSheet att = attendanceRepository.findByEmployeeAndClockOutTimeIsNull(emp)
                .orElseThrow(() -> new IllegalStateException("No active clock-in session found!"));
        LocalDateTime now = LocalDateTime.now();
        att.setClockOutTime(now);
        if (att.getClockOutTime().isBefore(att.getClockInTime())) {
            throw new IllegalArgumentException("Clock Out time cannot be before clock-in session!");
        }
        att.calculateAndSetWorkTimeInMinutes(now);
        att.calculateAndSetTotalHours();
        if (att.getTotalHours().compareTo(BigDecimal.valueOf(16)) > 0) {
            throw new IllegalArgumentException("Total worked time cannot be greater than 16 hours!");
        }
        System.out.println("Clock-out at: " + att.getClockOutTime() + ", Total hours: " + att.getTotalHours() + ", Break minutes: " + att.getBreakInMinutes() + ", Work time: " + att.getWorkTimeInMinutes());
        return attendanceRepository.save(att);
    }



    @Transactional
    public AttendanceSheet endBreak() {
        Employee emp = employeeService.getCurrentAuthenticatedEmployee();
        AttendanceSheet att = attendanceRepository.findByEmployeeAndClockOutTimeIsNullAndBreakStartTimeIsNotNullAndBreakEndTimeIsNull(emp)
                .orElseThrow(() -> new IllegalStateException("No active break found"));
        LocalDateTime breakEndTime = LocalDateTime.now();
        if (breakEndTime.isBefore(att.getBreakStartTime())) {
            throw new IllegalArgumentException("Break end time cannot be before break start time");
        }
        long breakMinutes = Duration.between(att.getBreakStartTime(), breakEndTime).toMinutes();
        att.setBreakInMinutes(att.getBreakInMinutes() + (int) breakMinutes);
        att.setBreakStartTime(null);
        att.setBreakEndTime(breakEndTime);
        att.calculateAndSetWorkTimeInMinutes(breakEndTime);
        System.out.println("Break ended at: " + breakEndTime + ", Duration: " + breakMinutes + " minutes");
        return attendanceRepository.save(att);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void updateActiveWorkTime() {
        List<AttendanceSheet> activeSheets = attendanceRepository.findAll().stream()
                .filter(sheet -> sheet.getClockOutTime() == null && sheet.getBreakStartTime() == null)
                .toList();
        LocalDateTime now = LocalDateTime.now();
        for (AttendanceSheet sheet : activeSheets) {
            sheet.calculateAndSetWorkTimeInMinutes(now);
            attendanceRepository.save(sheet);
        }
        if (!activeSheets.isEmpty()) {
            System.out.println("Updated workTimeInMinutes for " + activeSheets.size() + " active sessions at " + now);
        }
    }

    @Transactional(readOnly = true)
    public WeeklyTimesheetDTO getWeeklyReport(Employee employee, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<AttendanceSheet> sheets = attendanceRepository.findByEmployeeAndDateBetween(employee, weekStart, weekEnd);

        Map<LocalDate, AttendanceSheet> sheetMap = new HashMap<>();
        for (AttendanceSheet sheet : sheets) {
            sheetMap.put(sheet.getDate(), sheet);
        }

        WeeklyTimesheetDTO dto = new WeeklyTimesheetDTO();

        // Fill data for each day of the week, using 0 for missing days
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            AttendanceSheet sheet = sheetMap.get(currentDate);
            DailyTimesheetDTO daily;
            if (sheet != null && sheet.getClockOutTime() != null) {
                daily = new DailyTimesheetDTO(sheet.getTotalHours(), sheet.getBreakInMinutes());
                dto.setTotalWorkedHours(dto.getTotalWorkedHours().add(sheet.getTotalHours()));
                dto.setTotalBreakMinutes(dto.getTotalBreakMinutes() + sheet.getBreakInMinutes());
            } else {
                daily = new DailyTimesheetDTO();
            }
            dto.getDays().put(dayName, daily);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<EmployeeWeeklyTimesheetDTO> getAllEmployeesWeeklyReport(LocalDate weekStart) {
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeWeeklyTimesheetDTO> timesheets = new ArrayList<>();

        for (Employee emp : employees) {
            WeeklyTimesheetDTO weeklyDTO = getWeeklyReport(emp, weekStart);
            EmployeeWeeklyTimesheetDTO employeeTimesheet = new EmployeeWeeklyTimesheetDTO(
                    emp.getEmployeeId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    weeklyDTO
            );
            timesheets.add(employeeTimesheet);
        }

        return timesheets;
    }
}