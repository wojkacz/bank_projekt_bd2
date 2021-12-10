package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import java.sql.Date;

public class Loan_Info {
    private Long loan_info_id;
    private Float amount;
    private Float interest;
    private Integer loan_length;
    private Integer loan_installment;
    private Long account_id;
    private Date date;

    public Loan_Info(Long loan_info_id, Float amount, Float interest, Integer loan_length, Integer loan_installment, Long account_id, Date date) {
        this.loan_info_id = loan_info_id;
        this.amount = amount;
        this.interest = interest;
        this.loan_length = loan_length;
        this.loan_installment = loan_installment;
        this.account_id = account_id;
        this.date = date;
    }

    public Loan_Info(Float amount, Float interest, Integer loan_length, Integer loan_installment, Long account_id, Date date) {
        this.amount = amount;
        this.interest = interest;
        this.loan_length = loan_length;
        this.loan_installment = loan_installment;
        this.account_id = account_id;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Loan_Info{" +
                "loan_info_id=" + loan_info_id +
                ", amount=" + amount +
                ", interest=" + interest +
                ", loan_length=" + loan_length +
                ", loan_installment=" + loan_installment +
                ", account_id=" + account_id +
                ", date=" + date +
                '}';
    }
}
