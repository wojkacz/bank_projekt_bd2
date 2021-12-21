package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Loan_Info {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long loan_info_id;
    private Double amount;
    private int loan_length;
    private Long account_id;
    private LocalDate date;

    public Loan_Info() {
    }

    public Loan_Info(Long account_id, Double amount, int loan_length, LocalDate date){
        this.account_id = account_id;
        this.amount = amount;
        this.loan_length = loan_length;
        this.date = date;
    }

    public Long getLoan_info_id() {
        return loan_info_id;
    }

    public void setLoan_info_id(Long loan_info_id) {
        this.loan_info_id = loan_info_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getLoan_length() {
        return loan_length;
    }

    public void setLoan_length(int loan_length) {
        this.loan_length = loan_length;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
