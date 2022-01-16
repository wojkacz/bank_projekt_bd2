package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.TransferRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer_Info;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer_InfoRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserRepository;
import org.springframework.core.env.Environment;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;


@Configuration
public class Config implements EnvironmentAware {

    @Autowired
    private static Environment environment;

    public static final boolean testMode = true;

    @Bean
    public JavaMailSender getJavaMailSender()
    {
        String login = environment.getProperty("mailSender.login");
        String password = environment.getProperty("mailSender.password");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(login);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                login, password);
                    }
                });
        return mailSender;
    }


    @Bean
    CommandLineRunner commandLineRunner(CurrencyRepository currencyRepository, CurrencyService currencyService, UserRepository userRepository, AccountRepository accountRepository, Transfer_InfoRepository transferInfoRepository, TransferRepository transferRepository) {
      return args -> {
          Currency USD = new Currency("USD");
          Currency GBP = new Currency("GBP");
          Currency EUR = new Currency("EUR");

          // Login - admin         Password - admin
          User admin = new User("Jan", "Kowalski", "admin", "21232f297a57a5a743894a0e4a801fc3");
          admin.setPermission_level(1);
          admin.setActivated(true);

          // Login - test@test     Password - 12345
          User user = new User("Adam", "Kowalczyk", "test@test", "827ccb0eea8a706c4c34a16891f84e7b");
          user.setActivated(true);

          Account acc1 = new Account(2L);
          acc1.setBalance_pln(2123.0);
          acc1.setBalance_usd(1234.0);
          acc1.setBalance_euro(4500.99);
          acc1.setBalace_pound(199.55);

          Account acc2 = new Account(2L);

          Transfer_Info ti1 = new Transfer_Info(2L, 1L, 0L, 500.99, LocalDate.now().minusDays(1));
          Transfer_Info ti2 = new Transfer_Info(2L, 1L, 1L, 123.33, LocalDate.now().minusDays(3));
          Transfer_Info ti3 = new Transfer_Info(1L, 2L, 2L, 15.0, LocalDate.now().minusDays(2));
          Transfer_Info ti4 = new Transfer_Info(1L, 2L, 3L, 12345.67, LocalDate.now().minusDays(4));

          Transfer t1 = new Transfer(1L);
          Transfer t2 = new Transfer(2L);
          Transfer t3 = new Transfer(3L);
          Transfer t4 = new Transfer(4L);

          if(testMode){
              currencyRepository.saveAllAndFlush(
                      List.of(USD, GBP, EUR)
              );
              currencyService.updateCurrencies();
              userRepository.saveAllAndFlush(List.of(admin, user));
              accountRepository.saveAllAndFlush(List.of(acc1, acc2));
              transferInfoRepository.saveAllAndFlush(List.of(ti1, ti2, ti3, ti4));
              transferRepository.saveAllAndFlush(List.of(t1, t2, t3, t4));
          }
      };
    }

    @Override
    public void setEnvironment(Environment environment) {
        Config.environment = environment;
    }
}
