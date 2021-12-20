package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyRepository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;

import java.util.List;


@Configuration
public class Config {
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
