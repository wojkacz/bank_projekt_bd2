package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table
public class Transfer_Info {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long transfer_info_id;
    private Long sender_account_id;
    private Long receiver_account_id;
    private Float amount;
    private Date date;

    public Transfer_Info(Long transfer_info_id, Long sender_account_id, Long receiver_account_id, Float amount, Date date) {
        this.transfer_info_id = transfer_info_id;
        this.sender_account_id = sender_account_id;
        this.receiver_account_id = receiver_account_id;
        this.amount = amount;
        this.date = date;
    }

    public Transfer_Info(Long sender_account_id, Long receiver_account_id, Float amount, Date date) {
        this.sender_account_id = sender_account_id;
        this.receiver_account_id = receiver_account_id;
        this.amount = amount;
        this.date = date;
    }

    public Transfer_Info() {
    }

    @Override
    public String toString() {
        return "Transfer_Info{" +
                "transfer_info_id=" + transfer_info_id +
                ", sender_account_id=" + sender_account_id +
                ", receiver_account_id=" + receiver_account_id +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
