package pl.kaczmarek.naporowski.bank_projekt_bd2.Currency;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long currency_id;
    private String name;
    private Float sell_price;
    private Float buy_price;

    public Currency(Long currency_id, String name, Float sell_price, Float buy_price) {
        this.currency_id = currency_id;
        this.name = name;
        this.sell_price = sell_price;
        this.buy_price = buy_price;
    }

    public Currency(String name, Float sell_price, Float buy_price) {
        this.name = name;
        this.sell_price = sell_price;
        this.buy_price = buy_price;
    }

    public Currency() {
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency_id=" + currency_id +
                ", name='" + name + '\'' +
                ", sell_price=" + sell_price +
                ", buy_price=" + buy_price +
                '}';
    }
}
