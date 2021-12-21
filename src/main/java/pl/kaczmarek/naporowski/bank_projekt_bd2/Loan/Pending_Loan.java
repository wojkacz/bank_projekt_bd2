package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import javax.persistence.*;

@Entity
@Table
public class Pending_Loan {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long pending_loan_id;
    private Long loan_info_id;

    public Pending_Loan(Long pending_loan_id, Long loan_info_id) {
        this.pending_loan_id = pending_loan_id;
        this.loan_info_id = loan_info_id;
    }

    public Pending_Loan(Long loan_info_id) {
        this.loan_info_id = loan_info_id;
    }

    public Pending_Loan() {
    }

    @Override
    public String toString() {
        return "Pending_Loan{" +
                "pending_loan_id=" + pending_loan_id +
                ", loan_info_id=" + loan_info_id +
                '}';
    }

    public Long getPending_loan_id() {
        return pending_loan_id;
    }

    public void setPending_loan_id(Long pending_loan_id) {
        this.pending_loan_id = pending_loan_id;
    }

    public Long getLoan_info_id() {
        return loan_info_id;
    }

    public void setLoan_info_id(Long loan_info_id) {
        this.loan_info_id = loan_info_id;
    }
}
