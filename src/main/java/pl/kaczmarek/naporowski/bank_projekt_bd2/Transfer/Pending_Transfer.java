package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import javax.persistence.*;

@Entity
@Table
public class Pending_Transfer {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long pending_transfer_id;
    private Long transfer_info_id;

    public Pending_Transfer(Long pending_transfer_id, Long transfer_info_id) {
        this.pending_transfer_id = pending_transfer_id;
        this.transfer_info_id = transfer_info_id;
    }

    public Pending_Transfer(Long transfer_info_id) {
        this.transfer_info_id = transfer_info_id;
    }

    public Pending_Transfer() {
    }

    @Override
    public String toString() {
        return "Pending_Transfer{" +
                "pending_transfer_id=" + pending_transfer_id +
                ", transfer_info_id=" + transfer_info_id +
                '}';
    }

    public Long getPending_transfer_id() {
        return pending_transfer_id;
    }

    public void setPending_transfer_id(Long pending_transfer_id) {
        this.pending_transfer_id = pending_transfer_id;
    }

    public Long getTransfer_info_id() {
        return transfer_info_id;
    }

    public void setTransfer_info_id(Long transfer_info_id) {
        this.transfer_info_id = transfer_info_id;
    }
}
