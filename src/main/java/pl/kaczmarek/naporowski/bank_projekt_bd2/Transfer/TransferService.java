package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransferService {
    Pending_TransferRepository pendingTransferRepository;
    TransferRepository transferRepository;
    Transfer_InfoRepository transferInfoRepository;
    AccountService accountService;
    CurrencyService currencyService;

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

        Account receiver = accountService.getAccountByID(receiver_account_id);
        if(receiver == null)
            return 2; // niepoprawne id odbiorcy

        if(amount <= 0.0)
            return 3; // Niepoprawna kwota

        Double amountPLN;
        if(currency_id.equals(0L)) {
            if(sender.getBalance_pln() < amount)
                return 5; // Za maly stan konta
            amountPLN = amount;
        }
        else {
            Currency currency = currencyService.getCurrencyById(currency_id);
            if(currency == null) return 4; // Niepoprawne id waluty

            String curName = currency.getName();
            switch (curName) {
                case "USD":
                    if (sender.getBalance_usd() < amount)
                        return 5; // za maly stan konta
                    break;
                case "GBP":
                    if (sender.getBalace_pound() < amount)
                        return 5; // za maly stan konta
                    break;
                case "EUR":
                    if (sender.getBalance_euro() < amount)
                        return 5; // za maly stan konta
                    break;
                default:
                    return 6; // Nie znaleziono waluty
            }

            amountPLN = amount * currency.getBuy_price();
        }

        Transfer_Info transfer_info = new Transfer_Info(sender_account_id, receiver_account_id, currency_id, amount, date);
        transfer_info = transferInfoRepository.saveAndFlush(transfer_info);

        if(amountPLN > 1000.0) {
            pendingTransferRepository.save(new Pending_Transfer(transfer_info.getTransfer_info_id()));
            return 7; // Dodany do listy oczekujących
        }

        sendTransfer(transfer_info.getTransfer_info_id(), null);
        return 0;
    }

    @Transactional
    public int sendTransfer(Long transfer_info_id, Long employee_id){
        if(!transferInfoRepository.existsById(transfer_info_id))
            return 1; // Nie istnieje takie id

        Transfer transfer = new Transfer(transfer_info_id, employee_id);
        Transfer_Info transfer_info = transferInfoRepository.getById(transfer_info_id);

        Account sender = accountService.getAccountByID(transfer_info.getSender_account_id());
        Account receiver = accountService.getAccountByID(transfer_info.getReceiver_account_id());

        Long currency_id = transfer_info.getCurrency_id();
        Double amount = transfer_info.getAmount();

        if(currency_id.equals(0L)) {
            if(sender.getBalance_pln() < amount)
                return 2; // Za maly stan konta
            sender.setBalance_pln(sender.getBalance_pln() - amount);
            receiver.setBalance_pln(receiver.getBalance_pln() + amount);
        }
        else {
            Double amountPLN;
            Currency currency = currencyService.getCurrencyById(currency_id);
            if(currency == null) return 3; // Niepoprawne id waluty

            String curName = currency.getName();
            switch (curName) {
                case "USD":
                    if (sender.getBalance_usd() < amount)
                        return 2; // Za maly stan konta

                    amountPLN = amount * currency.getBuy_price();
                    sender.setBalance_usd(sender.getBalance_usd() - amount);
                    receiver.setBalance_pln(receiver.getBalance_pln() + amountPLN);
                    break;
                case "GBP":
                    if (sender.getBalace_pound() < amount)
                        return 2; // Za maly stan konta

                    amountPLN = amount * currency.getBuy_price();
                    sender.setBalace_pound(sender.getBalace_pound() - amount);
                    receiver.setBalance_pln(receiver.getBalance_pln() + amountPLN);
                    break;
                case "EUR":
                    if (sender.getBalance_euro() < amount)
                        return 2; // Za maly stan konta

                    amountPLN = amount * currency.getBuy_price();
                    sender.setBalance_euro(sender.getBalance_euro() - amount);
                    receiver.setBalance_pln(receiver.getBalance_pln() + amountPLN);
                    break;
                default:
                    return 4; // Niepoprawna waluta
            }
        }
        accountService.saveAccounts(List.of(sender, receiver));
        transferRepository.saveAndFlush(transfer);
        return 0;
    }

    public int acceptTransfer(Long pending_transfer_id, Long employee_id){
        Optional<Pending_Transfer> pendingOptional = pendingTransferRepository.findPendingTransferByID(pending_transfer_id);
        if(pendingOptional.isEmpty())
            return 7;

        Pending_Transfer pending_transfer = pendingOptional.get();
        int result = sendTransfer(pending_transfer.getTransfer_info_id(), employee_id);
        if(result == 0) pendingTransferRepository.delete(pending_transfer);
        return result;
    }

    public List<Pending_Transfer> getPendingTransfers(){
        return pendingTransferRepository.findAll();
    }

    public Transfer_Info getInfoById(Long id){
        if(!transferRepository.existsById(id)) return null;

        Transfer t = transferRepository.getById(id);
        return transferInfoRepository.getById(t.getTransfer_info_id());
    }

    public Transfer_Info getInfoByPendingId(Long id){
        if(!pendingTransferRepository.existsById(id)) return null;

        Pending_Transfer pt = pendingTransferRepository.getById(id);
        return transferInfoRepository.getById(pt.getTransfer_info_id());
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
