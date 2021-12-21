package pl.kaczmarek.naporowski.bank_projekt_bd2.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Long> getAccountsId(Long user_id){
        List<Long> result = new ArrayList<>();
        List<Account> accounts = accountRepository.findAll();
        for(Account account : accounts)
            if(account.getUser_id().equals(user_id))
                result.add(account.getAccount_id());
        return result;
    }

    public List<Account> getAccounts(Long user_id){
        List<Account> result = new ArrayList<>();
        List<Account> accounts = accountRepository.findAll();
        for(Account account : accounts)
            if(account.getUser_id().equals(user_id))
                result.add(account);
        return result;
    }

    public void createNewAccount(Long user_id){
        accountRepository.save(new Account(user_id));
    }

    public int deleteAccount(Long user_id, Long account_id){
        Account account;
        if(accountRepository.existsById(account_id)) {
            account = accountRepository.getById(account_id);
            if(account.getUser_id().equals(user_id)){
                if(account.getBalance_pln().equals(0.0) && account.getBalance_euro().equals(0.0) && account.getBalace_pound().equals(0.0) && account.getBalance_usd().equals(0.0)){
                    accountRepository.delete(account);
                    return 0; // Wykonano pomyslnie
                }
                return 3; // Stan konta nie jest zerowy
            }
            return 2; // Uzytkownik nie jest wlascicielem tego konta
        }
        return 1; // Podane konto nie istnieje

    }

    public Account getAccountByID(Long Id){
        if(accountRepository.existsById(Id))
            return accountRepository.getById(Id);
        return null;
    }

    public void giveMoney(Long id){
        Account acc = accountRepository.getById(id);
        Double money = 10000.0;
        acc.setBalace_pound(money);
        acc.setBalance_euro(money);
        acc.setBalance_usd(money);
        acc.setBalance_pln(money);
        accountRepository.save(acc);
    }

    public int addBalanceToAccount(Long id, Double amount){
        Account account;
        if(accountRepository.existsById(id)) {
            account = accountRepository.getById(id);
            account.setBalance_pln(account.getBalance_pln() + amount);
            accountRepository.saveAndFlush(account);
            return 0;
        }
        return 1; // nie istnieje
    }

    public void saveAccounts(List<Account> accs){
        accountRepository.saveAllAndFlush(accs);
    }

    public int payInstallement(Long account_id, Double amount){
        if(!accountRepository.existsById(account_id))
            return 1; // Nie istnieje

        Account account = accountRepository.getById(account_id);
        if(account.getBalance_pln() < amount)
            return 2; // Nie ma wystarczajaco na koncie

        account.setBalance_pln(account.getBalance_pln() - amount);
        accountRepository.saveAndFlush(account);
        return 0;
    }
}
