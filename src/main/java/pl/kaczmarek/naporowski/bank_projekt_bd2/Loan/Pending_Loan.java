package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

public class Pending_Loan {
    private Long pending_loan_id;
    private Long loan_info_id;

    public Pending_Loan(Long pending_loan_id, Long loan_info_id) {
        this.pending_loan_id = pending_loan_id;
        this.loan_info_id = loan_info_id;
    }

    public Pending_Loan(Long loan_info_id) {
        this.loan_info_id = loan_info_id;
    }

    @Override
    public String toString() {
        return "Pending_Loan{" +
                "pending_loan_id=" + pending_loan_id +
                ", loan_info_id=" + loan_info_id +
                '}';
    }
}
