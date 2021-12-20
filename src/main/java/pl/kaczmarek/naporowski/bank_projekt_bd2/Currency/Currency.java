package pl.kaczmarek.naporowski.bank_projekt_bd2.Currency;

import javax.persistence.*;

@Entity
@Table
public class Currency {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long currency_id;
    private String name;
    private Double sell_price;
    private Double buy_price;

    public Currency(String name) {
        this.name = name;
        this.sell_price = 0.0;
        this.buy_price = 0.0;
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

    public Long getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(Long currency_id) {
        this.currency_id = currency_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSell_price() {
        return sell_price;
    }

    public void setSell_price(Double sell_price) {
        this.sell_price = sell_price;
    }

    public Double getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(Double buy_price) {
        this.buy_price = buy_price;
    }
}
