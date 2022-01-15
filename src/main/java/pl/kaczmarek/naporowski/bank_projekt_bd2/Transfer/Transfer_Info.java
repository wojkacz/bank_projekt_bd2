package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Transfer_Info {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long transfer_info_id;
    private Long sender_account_id;
    private Long receiver_account_id;
    private Long currency_id;
    private Double amount;
    private LocalDate date;

    public Transfer_Info(Long sender_account_id, Long receiver_account_id, Long currency_id, Double amount, LocalDate date) {
        this.sender_account_id = sender_account_id;
        this.receiver_account_id = receiver_account_id;
        this.amount = amount;
        this.date = date;
        this.currency_id = currency_id;
    }

    public Transfer_Info() {
    }

    @Override
    public String toString() {
        return "Transfer_Info{" +
                "transfer_info_id=" + transfer_info_id +
                ", sender_account_id=" + sender_account_id +
                ", receiver_account_id=" + receiver_account_id +
                ", currency_id=" + currency_id +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }

    public Long getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(Long currency_id) {
        this.currency_id = currency_id;
    }

    public Long getTransfer_info_id() {
        return transfer_info_id;
    }

    public void setTransfer_info_id(Long transfer_info_id) {
        this.transfer_info_id = transfer_info_id;
    }

    public Long getSender_account_id() {
        return sender_account_id;
    }

    public void setSender_account_id(Long sender_account_id) {
        this.sender_account_id = sender_account_id;
    }

    public Long getReceiver_account_id() {
        return receiver_account_id;
    }

    public void setReceiver_account_id(Long receiver_account_id) {
        this.receiver_account_id = receiver_account_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
