package MarcioTavares.Backend.Database.Service;


import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Repository.AttendanceRepository;
import MarcioTavares.Backend.Database.Repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class PayrollReportService {

    private EmployeeRepository employeeRepository;
    private AttendanceRepository attendanceRepository;

    public byte[] generatePayrollReport(LocalDate startDate, LocalDate endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payroll Report");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Employee ID", "Full Name", "Email", "Department",
                    "Total Days", "Total Hours", "Regular Hours", "Overtime Hours",
                    "Total Salary", "Period"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get all employees
            List<Employee> employees = employeeRepository.findAll();
            int rowNum = 1;

            for (Employee employee : employees) {
                Row row = sheet.createRow(rowNum++);

                // Get attendance data for this employee
                List<AttendanceSheet> attendanceList = attendanceRepository
                        .findByEmployeeAndDateBetween(employee, startDate, endDate);

                // Calculate totals
                PayrollData payrollData = calculatePayrollData(attendanceList);

                // Fill row data
                row.createCell(0).setCellValue(employee.getEmployeeId());
                row.createCell(1).setCellValue(employee.getFirstName() + " " + employee.getLastName());
                row.createCell(2).setCellValue(employee.getEmail());
                row.createCell(3).setCellValue(employee.getDepartmentName() != null ?
                        employee.getDepartmentName() : "N/A");
                row.createCell(4).setCellValue(payrollData.getTotalDays());
                row.createCell(5).setCellValue(payrollData.getTotalHours().doubleValue());
                row.createCell(6).setCellValue(payrollData.getRegularHours().doubleValue());
                row.createCell(7).setCellValue(payrollData.getOvertimeHours().doubleValue());
                row.createCell(8).setCellValue(payrollData.getTotalSalary().doubleValue());
                row.createCell(9).setCellValue(startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " - " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                // Apply data style to all cells
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating payroll report: " + e.getMessage());
        }
    }

    private PayrollData calculatePayrollData(List<AttendanceSheet> attendanceList) {
        PayrollData data = new PayrollData();

        BigDecimal totalHours = BigDecimal.ZERO;
        int totalDays = attendanceList.size();

        for (AttendanceSheet attendance : attendanceList) {
            if (attendance.getTotalHours() != null) {
                totalHours = totalHours.add(attendance.getTotalHours());
            }
        }
        // Calculate regular hours (8 hours per day max)
        BigDecimal regularHours = BigDecimal.valueOf(Math.min(totalDays * 8, totalHours.doubleValue()));

        // to Calculate overtime hours
        BigDecimal overtimeHours = totalHours.subtract(regularHours);
        if (overtimeHours.compareTo(BigDecimal.ZERO) < 0) {
            overtimeHours = BigDecimal.ZERO;
        }

        // Calculate total salary (assuming $15/hour regular, $22.5/hour overtime)
        BigDecimal regularRate = new BigDecimal("15.00");
        BigDecimal overtimeRate = new BigDecimal("22.50");

        BigDecimal totalSalary = regularHours.multiply(regularRate)
                .add(overtimeHours.multiply(overtimeRate));

        data.setTotalDays(totalDays);
        data.setTotalHours(totalHours);
        data.setRegularHours(regularHours);
        data.setOvertimeHours(overtimeHours);
        data.setTotalSalary(totalSalary);

        return data;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }



    // Inner class for payroll calculations
    private static class PayrollData {
        private int totalDays;
        private BigDecimal totalHours = BigDecimal.ZERO;
        private BigDecimal regularHours = BigDecimal.ZERO;
        private BigDecimal overtimeHours = BigDecimal.ZERO;
        private BigDecimal totalSalary = BigDecimal.ZERO;

        // Getters and setters
        public int getTotalDays() { return totalDays; }
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

        public BigDecimal getTotalHours() { return totalHours; }
        public void setTotalHours(BigDecimal totalHours) { this.totalHours = totalHours; }

        public BigDecimal getRegularHours() { return regularHours; }
        public void setRegularHours(BigDecimal regularHours) {
            this.regularHours = regularHours;
        }

        public BigDecimal getOvertimeHours() { return overtimeHours; }
        public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

        public BigDecimal getTotalSalary() { return totalSalary; }
        public void setTotalSalary(BigDecimal totalSalary) { this.totalSalary = totalSalary; }
    }

}
