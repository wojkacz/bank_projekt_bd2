package pl.kaczmarek.naporowski.bank_projekt_bd2.Currency;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public int addCurrency(String name){
        Optional<Currency> currencyOptional = currencyRepository.findByName(name);
        if(currencyOptional.isEmpty()) {
            currencyRepository.save(new Currency(name));
            return 0;
        }
        return 1;
    }

    public String getCurrenciesFromApi() {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://freecurrencyapi.net/api/v2/latest?apikey=9a1b77a0-61a7-11ec-8e36-61c2b2616836&base_currency=pln");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if(connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                    for (String line; (line = reader.readLine()) != null; )
                        result.append(line);
                }
                return result.toString();
            }
            return null;
        }
        catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public int updateCurrencies(){
        String response = getCurrenciesFromApi();
        if(response != null){
            try {
                JSONObject obj = new JSONObject(response);

                String usdValStr = obj.getJSONObject("data").getString("USD");
                String poundValStr = obj.getJSONObject("data").getString("GBP");
                String euroValStr = obj.getJSONObject("data").getString("EUR");

                Double usdSellValue = Double.parseDouble(usdValStr);
                Double poundSellValue = Double.parseDouble(poundValStr);
                Double euroSellValue = Double.parseDouble(euroValStr);

                Double usdBuyValue = 1.0 / usdSellValue;
                Double poundBuyValue = 1.0 / poundSellValue;
                Double euroBuyValue = 1.0 / euroSellValue;

                List<Currency> currencies = currencyRepository.findAll();
                for(Currency currency : currencies){
                    switch (currency.getName()) {
                        case "USD":
                            currency.setSell_price(usdSellValue);
                            currency.setBuy_price(usdBuyValue);
                            break;
                        case "GBP":
                            currency.setSell_price(poundSellValue);
                            currency.setBuy_price(poundBuyValue);
                            break;
                        case "EUR":
                            currency.setSell_price(euroSellValue);
                            currency.setBuy_price(euroBuyValue);
                            break;
                    }
                    currencyRepository.save(currency);
                }
                return 0; // Poprawnie

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 1; // Nie udalo sie pobrać informacji
    }

    public List<Currency> getCurrencies(){
        return currencyRepository.findAll();
    }

    public int deleteCurrency(Long id){
        if(!currencyRepository.existsById(id))
            return 1;
        currencyRepository.deleteById(id);
        return 0;
    }

    public int updateCurrency(Long id, String name, Double sellVal, Double buyVal){
        if(!currencyRepository.existsById(id))
            return 1; // nie istnieje

        Currency currency = currencyRepository.getById(id);
        if(name != null) {
            Optional<Currency> currencyOptional = currencyRepository.findByName(name);
            if(currencyOptional.isEmpty())
                currency.setName(name);
            return 2; // juz istnieje o takiej nazwie
        }

        if(sellVal != null) {
            if (sellVal <= 0.0) return 3; // za mala wartosc
            currency.setSell_price(sellVal);
        }

        if(buyVal != null) {
            if (buyVal <= 0.0) return 3; // za mala wartosc
            currency.setBuy_price(buyVal);
        }

        currencyRepository.save(currency);
        return 0;
    }
}
