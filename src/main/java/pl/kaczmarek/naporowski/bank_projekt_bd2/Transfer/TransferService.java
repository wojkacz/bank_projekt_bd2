package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserService;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferService {
    Pending_TransferRepository pendingTransferRepository;
    TransferRepository transferRepository;
    Transfer_InfoRepository transferInfoRepository;
    AccountService accountService;
    CurrencyService currencyService;

    boolean debug = false;

    @Autowired
    public TransferService(Pending_TransferRepository pendingTransferRepository, TransferRepository transferRepository, Transfer_InfoRepository transferInfoRepository, AccountService accountService, CurrencyService currencyService) {
        this.pendingTransferRepository = pendingTransferRepository;
        this.transferRepository = transferRepository;
        this.transferInfoRepository = transferInfoRepository;
        this.accountService = accountService;
        this.currencyService = currencyService;
    }

    public int makeTransfer(Long sender_account_id, Long receiver_account_id, Long currency_id, Double amount, LocalDate date){
        Account sender = accountService.getAccountByID(sender_account_id);
        if(sender == null)
            return 1; // niepoprawne id wysylajacego
        if(debug) System.out.println("[DEBUG] Sender found");


        Account receiver = accountService.getAccountByID(receiver_account_id);
        if(receiver == null)
            return 2; // niepoprawne id odbiorcy
        if(debug) System.out.println("[DEBUG] Receiver found");

        if(amount <= 0.0)
            return 3; // Niepoprawna kwota
        if(debug) System.out.println("[DEBUG] Value good");

        Double amountPLN;
        if(currency_id.equals(0L)) {
            if(debug) System.out.println("[DEBUG] Currency good - PLN");
            if(sender.getBalance_pln() < amount)
                return 5; // Za maly stan konta
            if(debug) System.out.println("[DEBUG] Balance good");
            amountPLN = amount;
            if(debug) System.out.println("[DEBUG] AmountPLN - " + amountPLN);
        }
        else {
            Currency currency = currencyService.getCurrencyById(currency_id);
            if(currency == null) return 4; // Niepoprawne id waluty
            if(debug) System.out.println("[DEBUG] Currency found");

            String curName = currency.getName();
            if(curName.equals("USD")) {
                if(debug) System.out.println("[DEBUG] Currency good - USD");
                if (sender.getBalance_usd() < amount)
                    return 5; // za maly stan konta
                if(debug) System.out.println("[DEBUG] Balance good");
            }

            else if(curName.equals("GBP")) {
                if(debug) System.out.println("[DEBUG] Currency good - GBP");
                if (sender.getBalace_pound() < amount)
                    return 5; // za maly stan konta
                if(debug) System.out.println("[DEBUG] Balance good");
            }

            else if(curName.equals("EUR")) {
                if(debug) System.out.println("[DEBUG] Currency good - EUR");
                if (sender.getBalance_euro() < amount)
                    return 5; // za maly stan konta
                if(debug) System.out.println("[DEBUG] Balance good");
            }

            amountPLN = amount * currency.getBuy_price();
            if(debug) System.out.println("[DEBUG] AmountPLN - " + amountPLN);
        }

        if(debug) System.out.println("[DEBUG] Creating transfer info");
        Transfer_Info transfer_info = new Transfer_Info(sender_account_id, receiver_account_id, currency_id, amount, date);
        if(debug) System.out.println("[DEBUG] Transfer Info good");

        if(debug) System.out.println("[DEBUG] Saving and flushing");
        transfer_info = transferInfoRepository.saveAndFlush(transfer_info);
        if(debug) System.out.println("[DEBUG] Done");

        if(debug) System.out.println("[DEBUG] Transfer_Info creating good - ID: " + transfer_info.getCurrency_id());

        if(amountPLN > 1000.0) {
            if(debug) System.out.println("[DEBUG] Amount more than 1000PLN");
            pendingTransferRepository.save(new Pending_Transfer(transfer_info.getTransfer_info_id()));
            if(debug) System.out.println("[DEBUG] Pending transfer added");
            return 6; // Dodany do listy oczekujących
        }

        if(debug) System.out.println("[DEBUG] Sending transfer lower than 1000 PLN");
        sendTransfer(transfer_info.getTransfer_info_id(), null);
        if(debug) System.out.println("[DEBUG] Sent");
        return 0;
    }

    @Transactional
    public int sendTransfer(Long transfer_info_id, Long employee_id){
        if(debug) System.out.println("[DEBUG] Sending started");
        if(!transferInfoRepository.existsById(transfer_info_id))
            return 1; // Nie istnieje takie id
        if(debug) System.out.println("[DEBUG] Transfer info found");

        Transfer transfer = new Transfer(transfer_info_id, employee_id);
        if(debug) System.out.println("[DEBUG] Transfer obj created");

        Transfer_Info transfer_info = transferInfoRepository.getById(transfer_info_id);
        if(debug) System.out.println("[DEBUG] Transfer info obj found");

        Account sender = accountService.getAccountByID(transfer_info.getSender_account_id());
        if(debug) System.out.println("[DEBUG] Sender account found");

        Account receiver = accountService.getAccountByID(transfer_info.getReceiver_account_id());
        if(debug) System.out.println("[DEBUG] Receiver account found");

        Long currency_id = transfer_info.getCurrency_id();
        Double amount = transfer_info.getAmount();
        if(debug) System.out.println("[DEBUG] Currency ID: " + currency_id + "; Amount: " + amount);

        if(currency_id.equals(0L)) {
            if(debug) System.out.println("[DEBUG] Found PLN");
            if(sender.getBalance_pln() < amount)
                return 2; // Za maly stan konta
            if(debug) System.out.println("[DEBUG] Balance good");
            if(debug) System.out.println("[DEBUG] Sending PLN");
            sender.setBalance_pln(sender.getBalance_pln() - amount);
            receiver.setBalance_pln(receiver.getBalance_pln() + amount);
            if(debug) System.out.println("[DEBUG] Sent PLN");
        }
        else {
            if(debug) System.out.println("[DEBUG] Found other than PLN");
            Double amountPLN;
            Currency currency = currencyService.getCurrencyById(currency_id);
            if(currency == null) return 3; // Niepoprawne id waluty
            if(debug) System.out.println("[DEBUG] Currency id good");

            String curName = currency.getName();
            if(curName.equals("USD")) {
                if(debug) System.out.println("[DEBUG] Found USD");
                if (sender.getBalance_usd() < amount)
                    return 2; // za maly stan konta
                if(debug) System.out.println("[DEBUG] Balance good");

                if(debug) System.out.println("[DEBUG] Sending");
                amountPLN = amount * currency.getBuy_price();
                sender.setBalance_usd(sender.getBalance_usd() - amount);
                receiver.setBalance_pln(receiver.getBalance_pln() + amountPLN);
                if(debug) System.out.println("[DEBUG] Sent");
            }

            else if(curName.equals("GBP")) {
                if(debug) System.out.println("[DEBUG] Found GBP");
                if (sender.getBalace_pound() < amount)
                    return 2; // za maly stan konta
                if(debug) System.out.println("[DEBUG] Balance good");

                if(debug) System.out.println("[DEBUG] Sending");
                amountPLN = amount * currency.getBuy_price();
                sender.setBalace_pound(sender.getBalace_pound() - amount);
                receiver.setBalance_pln(receiver.getBalance_pln() + amountPLN);
                if(debug) System.out.println("[DEBUG] Sent");
            }

            else if(curName.equals("EUR")) {
                if(debug) System.out.println("[DEBUG] Found EUR");
                if (sender.getBalance_euro() < amount)
                    return 2; // za maly stan konta
                if(debug) System.out.println("[DEBUG] Balance good");

                if(debug) System.out.println("[DEBUG] Sending");
                amountPLN = amount * currency.getBuy_price();
                sender.setBalance_euro(sender.getBalance_euro() - amount);
                receiver.setBalance_pln(receiver.getBalance_pln() + amountPLN);
                if(debug) System.out.println("[DEBUG] Sent");
            }
        }
        if(debug) System.out.println("[DEBUG] All good");
        transferRepository.saveAndFlush(transfer);
        if(debug) System.out.println("[DEBUG] Returning");
        return 0;
    }

    public int acceptTransfer(Long pending_transfer_id, Long employee_id){
        if(!pendingTransferRepository.existsById(pending_transfer_id))
            return 7; // Zle id

        Pending_Transfer pending_transfer = pendingTransferRepository.getById(pending_transfer_id);
        int result = sendTransfer(pending_transfer.getTransfer_info_id(), employee_id);
        if(result == 0) pendingTransferRepository.delete(pending_transfer);
        return result;
    }

    public List<Pending_Transfer> getPendingTransfers(){
        return pendingTransferRepository.findAll();
    }

    public Transfer_Info getInfoById(Long id){
        if(transferInfoRepository.existsById(id))
            return transferInfoRepository.getById(id);
        return null;
    }

    public int deletePendingTransfer(Long id){
        if(!pendingTransferRepository.existsById(id))
            return 1; // zle id

        Pending_Transfer pt = pendingTransferRepository.getById(id);
        transferInfoRepository.deleteById(pt.getTransfer_info_id());
        pendingTransferRepository.delete(pt);
        return 0;
    }

    public List<Transfer> getTransfers(Long account_id){
        List<Transfer> result = new ArrayList<>();
        List<Transfer> transfers = transferRepository.findAll();
        for(Transfer transfer : transfers){
            Transfer_Info transfer_info = transferInfoRepository.getById(transfer.getTransfer_info_id());
            if(transfer_info.getSender_account_id().equals(account_id) || transfer_info.getReceiver_account_id().equals(account_id))
                result.add(transfer);
        }
        return result;
    }
}
