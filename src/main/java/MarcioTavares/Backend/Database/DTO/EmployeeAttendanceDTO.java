package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data

    public class EmployeeAttendanceDTO {
        private String employeeId;
        private String firstName;
        private String lastName;
        private String email;
        private LocalDateTime clockInTime;
        private LocalDateTime clockOutTime;

        public EmployeeAttendanceDTO(String employeeId, String firstName, String lastName, String email, LocalDateTime clockInTime, LocalDateTime clockOutTime) {
            this.employeeId = employeeId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.clockInTime = clockInTime;
            this.clockOutTime = clockOutTime;
        }

        // Getters
        public String getEmployeeId() {
            return employeeId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public LocalDateTime getClockInTime() {
            return clockInTime;
        }

        public LocalDateTime getClockOutTime() {
            return clockOutTime;
        }

        // Setters (optional, but included for completeness)
        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setClockInTime(LocalDateTime clockInTime) {
            this.clockInTime = clockInTime;
        }

        public void setClockOutTime(LocalDateTime clockOutTime) {
            this.clockOutTime = clockOutTime;
        }
    }
