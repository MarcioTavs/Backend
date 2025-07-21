package MarcioTavares.Backend.Database.Controller;

import MarcioTavares.Backend.Database.Service.PayrollReportService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/payroll")
@AllArgsConstructor
public class PayrollController {

    private final PayrollReportService payrollReportService;


    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> generatePayrollReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            byte[] excelData = payrollReportService.generatePayrollReport(startDate, endDate);

            String filename = "payroll_report_" +
                    startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "_to_" +
                    endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/report/excel/current-month")
    public ResponseEntity<byte[]> generateCurrentMonthReport() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        return generatePayrollReport(startOfMonth, endOfMonth);
    }

    @GetMapping("/report/excel/last-month")
    public ResponseEntity<byte[]> generateLastMonthReport() {
        LocalDate now = LocalDate.now();
        LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endOfLastMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        return generatePayrollReport(startOfLastMonth, endOfLastMonth);
    }
}