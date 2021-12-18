package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transfer {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long transfer_id;
    private Long transfer_info_id;
    private Long employee_user_id;

    public Transfer(Long transfer_id, Long transfer_info_id, Long employee_user_id) {
        this.transfer_id = transfer_id;
        this.transfer_info_id = transfer_info_id;
        this.employee_user_id = employee_user_id;
    }

    public Transfer(Long transfer_info_id, Long employee_user_id) {
        this.transfer_info_id = transfer_info_id;
        this.employee_user_id = employee_user_id;
    }

    public Transfer() {
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transfer_id=" + transfer_id +
                ", transfer_info_id=" + transfer_info_id +
                ", employee_user_id=" + employee_user_id +
                '}';
    }
}
