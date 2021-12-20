package pl.kaczmarek.naporowski.bank_projekt_bd2.Account;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Account {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long account_id;
    private Double balance_pln;
    private Double balance_usd;
    private Double balance_euro;
    private Double balace_pound;

    private Long user_id;
    private Long loan_id;

    public Account(Long user_id) {
        this.user_id = user_id;
        this.balance_pln = 0.0;
        this.balace_pound = 0.0;
        this.balance_euro = 0.0;
        this.balance_usd = 0.0;
    }

    public Account() {}

    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + account_id +
                ", balance_pln=" + balance_pln +
                ", balance_usd=" + balance_usd +
                ", balance_euro=" + balance_euro +
                ", balace_pound=" + balace_pound +
                ", user_id=" + user_id +
                ", loan_id=" + loan_id +
                '}';
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public Double getBalance_pln() {
        return balance_pln;
    }

    public void setBalance_pln(Double balance_pln) {
        this.balance_pln = balance_pln;
    }

    public Double getBalance_usd() {
        return balance_usd;
    }

    public void setBalance_usd(Double balance_usd) {
        this.balance_usd = balance_usd;
    }

    public Double getBalance_euro() {
        return balance_euro;
    }

    public void setBalance_euro(Double balance_euro) {
        this.balance_euro = balance_euro;
    }

    public Double getBalace_pound() {
        return balace_pound;
    }

    public void setBalace_pound(Double balace_pound) {
        this.balace_pound = balace_pound;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(Long loan_id) {
        this.loan_id = loan_id;
    }
}
