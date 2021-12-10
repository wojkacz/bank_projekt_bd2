package pl.kaczmarek.naporowski.bank_projekt_bd2.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Account {
    private Long account_id;
    private Integer balance_pln;
    private Integer balance_usd;
    private Integer balance_euro;
    private Integer balace_pound;
    private List<Long> transfer_id = new ArrayList<>();
    private Long user_id;
    private Long loan_id;

    public Account(Long account_id, Long user_id) {
        this.account_id = account_id;
        this.user_id = user_id;
    }

    public Account(Long user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + account_id +
                ", balance_pln=" + balance_pln +
                ", balance_usd=" + balance_usd +
                ", balance_euro=" + balance_euro +
                ", balace_pound=" + balace_pound +
                ", transfer_id=" + transfer_id +
                ", user_id=" + user_id +
                ", loan_id=" + loan_id +
                '}';
    }
}
