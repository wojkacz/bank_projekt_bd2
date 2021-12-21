package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import javax.persistence.*;

@Entity
@Table
public class Loan {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long loan_id;
    private Long loan_info_id;
    private int paid_installments;
    private Long employee_user_id;

    public Loan(Long loan_info_id, Long employee_user_id) {
        this.loan_info_id = loan_info_id;
        this.paid_installments = 0;
        this.employee_user_id = employee_user_id;
    }

    public Loan() {
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

    public Long getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(Long loan_id) {
        this.loan_id = loan_id;
    }

    public Long getLoan_info_id() {
        return loan_info_id;
    }

    public void setLoan_info_id(Long loan_info_id) {
        this.loan_info_id = loan_info_id;
    }

    public int getPaid_installments() {
        return paid_installments;
    }

    public void setPaid_installments(int paid_installments) {
        this.paid_installments = paid_installments;
    }

    public Long getEmployee_user_id() {
        return employee_user_id;
    }

    public void setEmployee_user_id(Long employee_user_id) {
        this.employee_user_id = employee_user_id;
    }
}
