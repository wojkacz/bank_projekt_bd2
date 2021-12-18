package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
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
}
