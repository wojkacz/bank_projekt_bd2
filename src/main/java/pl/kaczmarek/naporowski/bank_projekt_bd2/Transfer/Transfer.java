package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
public class Transfer implements Serializable {

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

    public Transfer(Long transfer_info_id){
        this.transfer_info_id = transfer_info_id;
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

    public Long getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(Long transfer_id) {
        this.transfer_id = transfer_id;
    }

    public Long getTransfer_info_id() {
        return transfer_info_id;
    }

    public void setTransfer_info_id(Long transfer_info_id) {
        this.transfer_info_id = transfer_info_id;
    }

    public Long getEmployee_user_id() {
        return employee_user_id;
    }

    public void setEmployee_user_id(Long employee_user_id) {
        this.employee_user_id = employee_user_id;
    }
}
