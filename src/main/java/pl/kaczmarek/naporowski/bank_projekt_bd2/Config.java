package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserRepository;

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


    @Bean
    CommandLineRunner commandLineRunner(CurrencyRepository currencyRepository, CurrencyService currencyService, UserRepository userRepository, AccountRepository accountRepository) {
      return args -> {
          Currency USD = new Currency("USD");
          Currency GBP = new Currency("GBP");
          Currency EUR = new Currency("EUR");
          currencyRepository.saveAllAndFlush(
                  List.of(USD, GBP, EUR)
          );
          currencyService.updateCurrencies();

          User user = new User("Jan", "Kowalski", "admin", "21232f297a57a5a743894a0e4a801fc3");
          user.setPermission_level(1);
          user.setActivated(true);

          userRepository.save(user);

          Account acc1 = new Account(1L);
          Account acc2 = new Account(1L);
          Account acc3 = new Account(1L);

          acc1.setBalance_pln(123.0);
          acc1.setBalace_pound(12.3);
          acc1.setBalance_usd(0.99);

          acc2.setBalance_pln(92139.0);
          acc2.setBalance_euro(39.99);

          acc3.setBalance_pln(21.37);
          accountRepository.saveAll(List.of(
                  acc1, acc2, acc3
          ));
      };
    }

}
