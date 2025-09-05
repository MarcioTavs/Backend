package MarcioTavares.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class  OnTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnTrackApplication.class, args);
	}

}
