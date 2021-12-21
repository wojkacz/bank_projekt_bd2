package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.List;
import java.util.Properties;


@Configuration
public class Config {

    @Bean
    public JavaMailSender getJavaMailSender()
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("bankappbd2@gmail.com");
        mailSender.setPassword("ABC123!@#");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                "bankappbd2@gmail.com", "ABC123!@#");// Specify the Username and the PassWord
                    }
                });



        return mailSender;
    }

    /*
    @Bean
    CommandLineRunner commandLineRunner(CurrencyRepository currencyRepository, CurrencyService currencyService) {
      return args -> {
          Currency USD = new Currency("USD");
          Currency GBP = new Currency("GBP");
          Currency EUR = new Currency("EUR");
          currencyRepository.saveAll(
                  List.of(USD, GBP, EUR)
          );
          currencyService.updateCurrencies();
      };
    }
    */
}
