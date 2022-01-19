package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Code.CodeService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Email.EmailService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.Loan;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.LoanService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.Loan_Info;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.Pending_Loan;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Token.TokenService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Pending_Transfer;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.TransferService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer_Info;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping
public class Controller {

    UserService userService;
    AccountService accountService;
    CurrencyService currencyService;
    TransferService transferService;
    LoanService loanService;
    TokenService tokenService;
    CodeService codeService;

    @Autowired
    public Controller(CodeService codeService, UserService userService, AccountService accountService, CurrencyService currencyService, TransferService transferService, LoanService loanService, TokenService tokenService) {
        this.userService = userService;
        this.accountService = accountService;
        this.currencyService = currencyService;
        this.transferService = transferService;
        this.loanService = loanService;
        this.tokenService = tokenService;
        this.codeService = codeService;
    }

    @PostMapping(path = "forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String login, String code){

        if(code == null){
            int result = userService.forgetPasswordSendCode(login);
            if(result == 0) return new ResponseEntity<>("Code sent!", HttpStatus.OK);
            else return new ResponseEntity<>("Incorrect login!", HttpStatus.NOT_FOUND);
        }

        int result = userService.forgetPasswordVerifyCode(login, code);
        if(result == 0) return new ResponseEntity<>("Password changed!", HttpStatus.OK);
        else if(result == 1) return new ResponseEntity<>("Incorrect login!", HttpStatus.NOT_FOUND);
        else if(result == 2) return new ResponseEntity<>("Incorrect code!", HttpStatus.EXPECTATION_FAILED);
        throw new IllegalStateException("Unknown error!");
    }

    @PostMapping(path = "login")
    public ResponseEntity<String> login(@RequestParam String login, @RequestParam String password){
        int result = userService.login(login, password);
        switch(result){
            case 0: // Poprawny login i haslo
                User user = userService.getUserByLogin(login);
                String token = tokenService.newToken(user.getUser_id());

                String text = "{ \"user-login-data\": { " +
                        "\"name\": \"" + user.getName() + "\"," +
                        "\"surname\": \"" + user.getSurname() + "\"," +
                        "\"login\": \"" + user.getLogin() + "\"," +
                        "\"permission_level\": " + user.getPermission_level() + "," +
                        "\"token\": \"" + token + "\" " +
                        "} }";
                return new ResponseEntity<>(text, HttpStatus.OK);

            case 1: // Niepoprawny login
                return new ResponseEntity<>("Incorrect login!", HttpStatus.NOT_FOUND);

            case 2: // Niepoprawne hasło
                return new ResponseEntity<>("Incorrect Password for user " + login, HttpStatus.EXPECTATION_FAILED);

            case 3: // Nieaktywny uzytkownik
                return new ResponseEntity<>("User inactive!", HttpStatus.METHOD_NOT_ALLOWED);

            default: // Inne
                throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "activate")
    public ResponseEntity<String> activateUser(@RequestParam String login, @RequestParam String code){
        int result = userService.activateUser(login, code);
        switch(result){
            case 0: // Poprawny login i kod
                User user = userService.getUserByLogin(login);
                return new ResponseEntity<>("User activated!", HttpStatus.OK);

            case 1: // Niepoprawny login
                return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);

            case 2: // Juz aktywowany
                return new ResponseEntity<>("User " + login + " is already activated!", HttpStatus.METHOD_NOT_ALLOWED);

            case 3: // Zły kod
                return new ResponseEntity<>("Incorrect activation code!", HttpStatus.EXPECTATION_FAILED);

            default: // Inne
                throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "register")
    public ResponseEntity<String> registerUser(@RequestParam String name, @RequestParam String surname, @RequestParam String login, @RequestParam String password){
        if(login.indexOf('@') == -1)
            return new ResponseEntity<>("Login must be email!", HttpStatus.EXPECTATION_FAILED);

        int result = userService.addNewUser(name, surname, login, password);
        switch (result) {
            case 0: // Poprawna rejestracja
                return new ResponseEntity<>("User registered!", HttpStatus.OK);

            case 1: // Login juz istnieje
                return new ResponseEntity<>("User with that login already exist!", HttpStatus.FOUND);

            default: // Inne
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
                //throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "updateUser")
    public ResponseEntity<String> updateUser(@RequestParam String tokenStr, @RequestParam String password_hash, String name, String surname, String login, String new_password_hash){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.NOT_FOUND);

        refreshToken(tokenStr);

        if(login != null && login.indexOf('@') == -1)
            return new ResponseEntity<>("Login must be email!", HttpStatus.EXPECTATION_FAILED);

        int result = userService.updateUser(userId, password_hash, name, surname, login, new_password_hash);;
        switch(result){
            case 0:
                return new ResponseEntity<>("User updated successfully", HttpStatus.OK );
            case 1:
                return new ResponseEntity<>("Incorrect password!", HttpStatus.FORBIDDEN);
            case 2:
                return new ResponseEntity<>("User with that id does not exist!", HttpStatus.FORBIDDEN);
            case 3:
                return new ResponseEntity<>("Name too short!", HttpStatus.NOT_ACCEPTABLE);
            case 4:
                return new ResponseEntity<>("Surname too short!", HttpStatus.NOT_ACCEPTABLE);
            case 5:
                return new ResponseEntity<>("Login too short!", HttpStatus.NOT_ACCEPTABLE);
            case 6:
                return new ResponseEntity<>("That login is already taken!", HttpStatus.CONFLICT);
            case 7:
                return new ResponseEntity<>("Password Hash too short!", HttpStatus.NOT_ACCEPTABLE);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(path = "refreshToken")
    public ResponseEntity<String> refreshToken(@RequestParam String tokenStr){
        int result = tokenService.refreshTokenTime(tokenStr);
        switch(result){
            case 0: // Poprawnie odswiezono
                return new ResponseEntity<>("Token refreshed!", HttpStatus.OK);

            case 1: // Nie znaleziono
                 return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

            default: // Inne
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(path = "getAccounts")
    public ResponseEntity<String> getAccounts(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        List<Account> accounts = accountService.getAccounts(userId);
        if(accounts.isEmpty()) return new ResponseEntity<>("No accounts found for this user!", HttpStatus.NOT_FOUND);

        StringBuilder sb = new StringBuilder();
        sb.append("{ \"data\": {").append("\"amount\": ").append(accounts.size()).append("}, ");
        for(int i = 0; i < accounts.size(); i++){
            sb.append("\"account_").append(i + 1).append("\": {");
            sb.append("\"account_id\": ").append(accounts.get(i).getAccount_id()).append(", ");
            sb.append("\"balance_pln\": ").append(accounts.get(i).getBalance_pln()).append(", ");
            sb.append("\"balance_euro\": ").append(accounts.get(i).getBalance_euro()).append(", ");
            sb.append("\"balance_pound\": ").append(accounts.get(i).getBalace_pound()).append(", ");
            sb.append("\"balance_usd\": ").append(accounts.get(i).getBalance_usd()).append(" ");
            if(i == accounts.size()-1) sb.append("} ");
            else sb.append("}, ");
        }
        sb.append('}');

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @GetMapping("connectionCheck")
    public ResponseEntity<String> checkConnection(){
        return new ResponseEntity<>("Connection good!", HttpStatus.OK);
    }

    @PostMapping(path = "createAccount")
    public ResponseEntity<String> createAccount(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        if(userService.isAdmin(userId))
            return new ResponseEntity<>("Admins cant create accounts!", HttpStatus.UNAUTHORIZED);

        accountService.createNewAccount(userId);
        return new ResponseEntity<>("Account created successfully!", HttpStatus.OK);
    }

    @DeleteMapping(path = "deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestParam String tokenStr, @RequestParam Long account_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        int result = accountService.deleteAccount(userId, account_id);
        switch(result){
            case 0: // Wykonano pomyslnie
                return new ResponseEntity<>("Account deleted successfully!", HttpStatus.OK);

            case 1: // Podane konto nie istnieje
                return new ResponseEntity<>("Account with this ID does not exist!", HttpStatus.NOT_FOUND);

            case 2: // Uzytkownik nie jest wlascicielem tego konta
                return new ResponseEntity<>("User is not the owner of this account!", HttpStatus.UNAUTHORIZED);

            case 3: // Stan konta nie jest zerowy
                return new ResponseEntity<>("Value of the account is not zero!", HttpStatus.EXPECTATION_FAILED);

            default: // Inne
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(path = "refreshCurrencies")
    private ResponseEntity<String> refreshCurrencies(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to refresh currencies!", HttpStatus.UNAUTHORIZED);

        int result = currencyService.updateCurrencies();
        switch (result){
            case 0:
                return new ResponseEntity<>("Values updated successfully", HttpStatus.OK);

            case 1: // Nie udalo sie pobrać
                return new ResponseEntity<>("Could not get currency values!", HttpStatus.EXPECTATION_FAILED);

            default: // Inne
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(path = "getCurrencies")
    private ResponseEntity<String> getCurrencies(){
        List<Currency> currencies = currencyService.getCurrencies();

        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for(int i = 0; i < currencies.size(); i++){
            sb.append("\"").append(currencies.get(i).getName()).append("\": {");
            sb.append("\"currency_id\": ").append(currencies.get(i).getCurrency_id()).append(", ");
            sb.append("\"buy_price\": ").append(currencies.get(i).getBuy_price()).append(", ");
            sb.append("\"sell_price\": ").append(currencies.get(i).getSell_price()).append(" ");
            if(i == currencies.size()-1) sb.append("} ");
            else sb.append("}, ");
        }
        sb.append('}');

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "addCurrency")
    private ResponseEntity<String> addCurrency(@RequestParam String tokenStr, @RequestParam String name){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to add currencies!", HttpStatus.UNAUTHORIZED);

        int result = currencyService.addCurrency(name);
        switch(result){
            case 0:
                return new ResponseEntity<>("Currency added successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Currency with that name already exist!", HttpStatus.FOUND);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }

    }

    @DeleteMapping(path = "deleteCurrency")
    private ResponseEntity<String> deleteCurrency(@RequestParam String tokenStr, @RequestParam Long id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to delete currencies!", HttpStatus.UNAUTHORIZED);

        int result = currencyService.deleteCurrency(id);
        switch(result){
            case 0:
                return new ResponseEntity<>("Currency deleted successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Currency with that id does not exist!", HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(path = "exchangeCurrency")
    private ResponseEntity<String> exchangeCurrency(@RequestParam String tokenStr, @RequestParam Long accountID, @RequestParam Double amount, @RequestParam String currencyFromName, @RequestParam String currencyToName){

        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        Account acc = accountService.getAccountByID(accountID);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        if(currencyFromName.equals(currencyToName))
            return new ResponseEntity<>("Cant exchange the same currencies!", HttpStatus.CONFLICT);

        Long currencyFromId;
        if(currencyFromName.equals("PLN")) currencyFromId = 0L;
        else currencyFromId = currencyService.getIDByName(currencyFromName);
        if(currencyFromId == null) return new ResponseEntity<>("Incorrect From currency [Has to be one of PLN, GBP, USD or EUR]!", HttpStatus.BAD_REQUEST);

        Long currencyToId;
        if(currencyToName.equals("PLN")) currencyToId = 0L;
        else currencyToId = currencyService.getIDByName(currencyToName);
        if(currencyToId == null) return new ResponseEntity<>("Incorrect To currency [Has to be one of PLN, GBP, USD or EUR]!", HttpStatus.BAD_REQUEST);

        if(amount < 0.01) return new ResponseEntity<>("Amount to be exchanged cant be smaller than 0.01!", HttpStatus.NOT_ACCEPTABLE);

        Currency toCur = currencyService.getCurrencyById(currencyToId);
        Currency fromCur = currencyService.getCurrencyById(currencyFromId);

        int result = accountService.exchangeCurrency(accountID, amount, fromCur, toCur);
        switch(result){
            case 0:
                return new ResponseEntity<>("Currency exchanged successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Insufficient balance!", HttpStatus.METHOD_NOT_ALLOWED);
            case 2:
                return new ResponseEntity<>("Amount after exchange cant be smaller than 0.01!", HttpStatus.NOT_ACCEPTABLE);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(path = "updateCurrency")
    private ResponseEntity<String> updateCurrency(@RequestParam String tokenStr, @RequestParam String name, Double sellVal, Double buyVal){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.UNAUTHORIZED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to update currencies!", HttpStatus.UNAUTHORIZED);

        Long id;
        if(name.equals("PLN")) id = 0L;
        else id = currencyService.getIDByName(name);

        int result = currencyService.updateCurrency(id, sellVal, buyVal);
        switch(result){
            case 0:
                return new ResponseEntity<>("Currency updated successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Currency with that id does not exist!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Value is too small!", HttpStatus.EXPECTATION_FAILED);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(path = "sendTransfer")
    private ResponseEntity<String> sendTransfer(@RequestParam String tokenStr, @RequestParam Long sender_account_id, @RequestParam Long receiver_account_id, @RequestParam String currencyName, @RequestParam Double amount){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        Account acc = accountService.getAccountByID(sender_account_id);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        Long currency_id;
        if(currencyName.equals("PLN")) currency_id = 0L;
        else currency_id = currencyService.getIDByName(currencyName);


        if(sender_account_id.equals(receiver_account_id))
            return new ResponseEntity<>("You cant send transfer to the same account!", HttpStatus.CONFLICT);

        int result = transferService.makeTransfer(sender_account_id, receiver_account_id, currency_id, amount, LocalDate.now());
        switch(result){
            case 0:
                return new ResponseEntity<>("Transfer sent successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Incorrect sender ID!", HttpStatus.SEE_OTHER);
            case 2:
                return new ResponseEntity<>("Incorrect receiver ID!", HttpStatus.CONFLICT);
            case 3:
                return new ResponseEntity<>("Incorrect transfer value!", HttpStatus.METHOD_NOT_ALLOWED);
            case 4:
                return new ResponseEntity<>("Currency id is incorrect!", HttpStatus.FORBIDDEN);
            case 5:
                return new ResponseEntity<>("Transfer value cant be higher than account balance!", HttpStatus.NOT_ACCEPTABLE);
            case 6:
                return new ResponseEntity<>("Incorrect currency [Has to be one of PLN, GPB, USD or EUR]!", HttpStatus.BAD_REQUEST);
            case 7:
                return new ResponseEntity<>("Transfer added to pending list!", HttpStatus.ACCEPTED);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(path = "getPendingTransfers")
    private ResponseEntity<String> getPendingTransfers(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to get pending transfers!", HttpStatus.UNAUTHORIZED);

        refreshToken(tokenStr);

        List<Pending_Transfer> pending_transfers = transferService.getPendingTransfers();
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"data\": {").append("\"amount\": ").append(pending_transfers.size()).append("}, ");
        for(int i = 0; i < pending_transfers.size(); i++){
            Transfer_Info ti = transferService.getInfoByPendingId(pending_transfers.get(i).getPending_transfer_id());

            String curName;
            if(ti.getCurrency_id().equals(0L)) curName = "PLN";
            else curName = currencyService.getCurrencyById(ti.getCurrency_id()).getName();

            sb.append("\"pending_transfer_").append(i + 1).append("\": {");
            sb.append("\"pending_transfer_id\": ").append(pending_transfers.get(i).getPending_transfer_id()).append(", ");
            sb.append("\"sender_account_id\": ").append(ti.getSender_account_id()).append(", ");
            sb.append("\"receiver_account_id\": ").append(ti.getReceiver_account_id()).append(", ");
            sb.append("\"currency\": ").append(curName).append(", ");
            sb.append("\"amount\": ").append(ti.getAmount()).append(", ");
            sb.append("\"date\": \"").append(ti.getDate().toString()).append("\" ");

            if(i == pending_transfers.size()-1) sb.append("} ");
            else sb.append("}, ");
        }
        sb.append('}');

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "acceptTransfer")
    private ResponseEntity<String> acceptTransfer(@RequestParam String tokenStr, @RequestParam Long pending_transfer_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to accept pending transfers!", HttpStatus.UNAUTHORIZED);

        refreshToken(tokenStr);

        int result = transferService.acceptTransfer(pending_transfer_id, userId);
        switch(result){
            case 0:
                return new ResponseEntity<>("Transfer sent successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Incorrect Pending Transfer ID!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Too low balance", HttpStatus.FORBIDDEN);
            case 3:
            case 4:
                return new ResponseEntity<>("Currency id is incorrect!", HttpStatus.NOT_FOUND);
            case 5:
                return new ResponseEntity<>("Incorrect pending transfer id!", HttpStatus.EXPECTATION_FAILED);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping(path = "deleteTransfer")
    private ResponseEntity<String> deleteTransfer(@RequestParam String tokenStr, @RequestParam Long pending_transfer_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to delete pending transfers!", HttpStatus.UNAUTHORIZED);

        refreshToken(tokenStr);

        int result = transferService.deletePendingTransfer(pending_transfer_id);

        switch (result){
            case 0:
                return new ResponseEntity<>("Deleted!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Incorrect pending transfer id!", HttpStatus.EXPECTATION_FAILED);
            default:
                throw new IllegalStateException("Unknown error!");
        }
    }

    @GetMapping(path = "money")
    private void money(@RequestParam Long id){
        if(Config.testMode) accountService.giveMoney(id);
    }

    @GetMapping(path = "admin")
    private void admin(@RequestParam Long id){
        if(Config.testMode) userService.setAdmin(id);
    }

    @GetMapping(path = "getTransfers")
    private ResponseEntity<String> getTransfers(@RequestParam String tokenStr, @RequestParam Long account_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        Account acc = accountService.getAccountByID(account_id);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        List<Transfer> transfers = transferService.getTransfers(account_id);
        transfers.sort(Comparator.comparing(Transfer::getTransfer_id).reversed());
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"data\": {").append("\"amount\": ").append(transfers.size()).append("}, ");
        for(int i = 0; i < transfers.size(); i++){
            Transfer_Info ti = transferService.getInfoById(transfers.get(i).getTransfer_id());

            sb.append("\"transfer_").append(i + 1).append("\": {");
            sb.append("\"sender_account_id\": ").append(ti.getSender_account_id()).append(", ");
            sb.append("\"receiver_account_id\": ").append(ti.getReceiver_account_id()).append(", ");
            sb.append("\"currency_id\": ").append(ti.getCurrency_id()).append(", ");
            sb.append("\"amount\": ").append(ti.getAmount()).append(", ");
            sb.append("\"date\": \"").append(ti.getDate().toString()).append("\" ");

            if(i == transfers.size()-1) sb.append("} ");
            else sb.append("}, ");
        }
        sb.append('}');

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "takeLoan")
    private ResponseEntity<String> takeLoan(@RequestParam String tokenStr, @RequestParam Long account_id, @RequestParam Double amount, @RequestParam int loan_length){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        Account acc = accountService.getAccountByID(account_id);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        int result = loanService.takeLoan(account_id, amount, loan_length, LocalDate.now());
        switch(result){
            case 0:
                return new ResponseEntity<>("Loan added to pending loans!", HttpStatus.OK);

            case 1:
                return new ResponseEntity<>("Account does not exist!", HttpStatus.NOT_FOUND);

            case 2:
                return new ResponseEntity<>("Bad Loan Value!", HttpStatus.EXPECTATION_FAILED);

            case 3:
                return new ResponseEntity<>("Loan time Incorrect!", HttpStatus.EXPECTATION_FAILED);

            case 4:
                return new ResponseEntity<>("This user already has a loan!", HttpStatus.FORBIDDEN);

            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(path = "acceptLoan")
    private ResponseEntity<String> acceptLoan(@RequestParam String tokenStr, @RequestParam Long pending_loan_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to accept pending loans!", HttpStatus.UNAUTHORIZED);

        refreshToken(tokenStr);

        int result = loanService.acceptPendingLoan(pending_loan_id, userId);
        switch (result){
            case 0:
                return new ResponseEntity<>("Loan accepted!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Pending Loan ID is incorrect!", HttpStatus.CONFLICT);
            case 2:
                return new ResponseEntity<>("Loan informations not found!", HttpStatus.NOT_FOUND);
            case 3:
                return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
            case 4:
                return new ResponseEntity<>("This user already have a loan!", HttpStatus.FORBIDDEN);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping(path = "deletePendingLoan")
    private ResponseEntity<String> deletePendingLoan(@RequestParam String tokenStr, @RequestParam Long pending_loan_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to delete pending loans!", HttpStatus.UNAUTHORIZED);

        refreshToken(tokenStr);

        int result = loanService.deletePendingLoan(pending_loan_id);
        switch (result){
            case 0:
                return new ResponseEntity<>("Loan deleted!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Pending Loan ID is incorrect!", HttpStatus.FORBIDDEN);
            case 2:
                return new ResponseEntity<>("Loan informations not found!", HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(path = "getPendingLoans")
    private ResponseEntity<String> getPendingLoans(@RequestParam String tokenStr) {
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if (userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if (!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to see pending loans!", HttpStatus.UNAUTHORIZED);

        refreshToken(tokenStr);

        List<Pending_Loan> pending_loans = loanService.getPendingLoans();
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"data\": {").append("\"amount\": ").append(pending_loans.size()).append("}, ");
        for(int i = 0; i < pending_loans.size(); i++){
            Loan_Info li = loanService.getInfoByPendingID(pending_loans.get(i).getLoan_info_id());
            Account account = accountService.getAccountByID(li.getAccount_id());

            sb.append("\"pending_loan_").append(i + 1).append("\": {");
            sb.append("\"pending_loan_id\": ").append(pending_loans.get(i).getPending_loan_id()).append(", ");
            sb.append("\"account_id\": ").append(li.getAccount_id()).append(", ");
            sb.append("\"user_id\": ").append(account.getUser_id()).append(", ");
            sb.append("\"amount\": ").append(li.getAmount()).append(", ");
            sb.append("\"length\": ").append(li.getLoan_length()).append(", ");
            sb.append("\"to_pay_back\": ").append(li.getAmount() * 1.1).append(", ");
            sb.append("\"date\": \"").append(li.getDate().toString()).append("\" ");

            if(i == pending_loans.size()-1) sb.append("} ");
            else sb.append("}, ");
        }
        sb.append('}');

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "getMyLoanInfo")
    private ResponseEntity<String> getMyLoanInfo(@RequestParam String tokenStr, @RequestParam Long account_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        Account acc = accountService.getAccountByID(account_id);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        Loan loan = loanService.getLoanByAccountID(acc.getAccount_id());
        if(loan == null) return new ResponseEntity<>("You don't have any loan!", HttpStatus.NOT_FOUND);
        Loan_Info li = loanService.getInfoByLoanID(loan.getLoan_info_id());

        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append("\"my_loan_info\": {");
        sb.append("\"amount\": ").append(li.getAmount()).append(", ");
        sb.append("\"length\": ").append(li.getLoan_length()).append(", ");
        sb.append("\"to_pay_back_total\": ").append(li.getAmount() * 1.1).append(", ");
        sb.append("\"paid_installements\": ").append(loan.getPaid_installments()).append(", ");
        sb.append("\"date\": \"").append(li.getDate().toString()).append("\" } }");

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "payInstallement")
    private ResponseEntity<String> payInstallement(@RequestParam String tokenStr, @RequestParam Long account_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        refreshToken(tokenStr);

        Account acc = accountService.getAccountByID(account_id);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        if(loanService.getLoanByAccountID(account_id) == null)
            return new ResponseEntity<>("That account does not have loan!", HttpStatus.EXPECTATION_FAILED);

        Loan loan = loanService.getLoanByAccountID(account_id);
        int result = loanService.payInstallement(loan.getLoan_id());
        switch(result){
            case 0:
                return new ResponseEntity<>("Successfully paid installement!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Loan ID is incorrect!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Could not get Loan Info!", HttpStatus.EXPECTATION_FAILED);
            case 3:
                return new ResponseEntity<>("That account does not exist!", HttpStatus.NOT_FOUND);
            case 4:
                return new ResponseEntity<>("That account does not have enough money to pay installement!", HttpStatus.EXPECTATION_FAILED);
            case 6:
                return new ResponseEntity<>("Could not calculate loan installement value!", HttpStatus.EXPECTATION_FAILED);
            case 7:
                return new ResponseEntity<>("Successfully paid installement, loan has been bought back!", HttpStatus.OK);
            default:
                return new ResponseEntity<>("Unknown Error!", HttpStatus.BAD_GATEWAY);
        }
    }
}
