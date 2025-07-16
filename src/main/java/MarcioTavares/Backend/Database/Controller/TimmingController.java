package MarcioTavares.Backend.Database.Controller;


import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Service.TimingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@AllArgsConstructor
public class TimmingController {

    private final TimingService timingService;

    @PostMapping("/clockIn")
    public ResponseEntity<?> clockIn(){
        try{
            AttendanceSheet att = timingService.clockIn();
            return ResponseEntity.ok(att);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/clockOut")
    public ResponseEntity<?> clockOut(){
        try{
            AttendanceSheet att = timingService.clockOut();
            return ResponseEntity.ok(att);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
