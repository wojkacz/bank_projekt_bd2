package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

public class Loan {
    private Long loan_id;
    private Long loan_info_id;
    private Integer paid_installments;
    private Long employee_user_id;

    public Loan(Long loan_id, Long loan_info_id, Integer paid_installments, Long employee_user_id) {
        this.loan_id = loan_id;
        this.loan_info_id = loan_info_id;
        this.paid_installments = paid_installments;
        this.employee_user_id = employee_user_id;
    }

    public Loan(Long loan_info_id, Integer paid_installments, Long employee_user_id) {
        this.loan_info_id = loan_info_id;
        this.paid_installments = paid_installments;
        this.employee_user_id = employee_user_id;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loan_id=" + loan_id +
                ", loan_info_id=" + loan_info_id +
                ", paid_installments=" + paid_installments +
                ", employee_user_id=" + employee_user_id +
                '}';
    }
}
