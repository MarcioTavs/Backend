package MarcioTavares.Backend.Database.Service;


import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Repository.AttendanceRepository;
import MarcioTavares.Backend.Database.Repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TimingService {
    private final EmployeeService employeeService;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public AttendanceSheet clockIn(){
        Employee emp = employeeService.getCurrentAuthenticatedEmployee();

//        Employee emp = employeeRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));



        Optional<AttendanceSheet> att = attendanceRepository.findByEmployeeAndClockOutTimeIsNull(emp);

        if(att.isPresent()){
            throw new IllegalStateException("Sorry You are already logged in");
        }

        LocalDate date = LocalDate.now();

        if(attendanceRepository.existsByEmployeeAndDate(emp, date)){
            throw new IllegalStateException("You already have a work session for today");
        }

        AttendanceSheet attendanceSheet = new AttendanceSheet();
        attendanceSheet.setEmployee(emp);
        attendanceSheet.setDate(date);
        attendanceSheet.setClockInTime(LocalDateTime.now());
        return  attendanceRepository.save(attendanceSheet);

    }
    @Transactional
    public AttendanceSheet clockOut(){
        Employee emp = employeeService.getCurrentAuthenticatedEmployee();

        //Employee emp = employeeRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Employee not found"));

        AttendanceSheet att = attendanceRepository.findByEmployeeAndClockOutTimeIsNull(emp).orElseThrow(() -> new IllegalStateException("No active clock-in session found !\""));
        att.setClockOutTime(LocalDateTime.now());
        att.setBreakInMinutes(0);

        if(att.getClockOutTime().isBefore(att.getClockInTime())){
            throw new IllegalArgumentException("Clock Out time cannot be before clock-in session !");
        }
        att.calculateAndSetTotalHours();
        if(att.getTotalHours().compareTo(BigDecimal.valueOf(16)) > 0){
            throw new IllegalArgumentException("Total worked time cannot be greater than 16 hours !");
        }

        return attendanceRepository.save(att);

    }
}
