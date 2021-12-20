package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BankProjektBd2Application {

	public static void main(String[] args) {
		SpringApplication.run(BankProjektBd2Application.class, args);
	}

}
