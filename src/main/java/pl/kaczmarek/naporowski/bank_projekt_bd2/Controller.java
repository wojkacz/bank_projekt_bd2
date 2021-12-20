package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.Currency;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.LoanService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Token.TokenService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Pending_Transfer;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.TransferService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer_Info;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class Controller {

    UserService userService;
    AccountService accountService;
    CurrencyService currencyService;
    TransferService transferService;
    LoanService loanService;
    TokenService tokenService;

    @Autowired
    public Controller(UserService userService, AccountService accountService, CurrencyService currencyService, TransferService transferService, LoanService loanService, TokenService tokenService) {
        this.userService = userService;
        this.accountService = accountService;
        this.currencyService = currencyService;
        this.transferService = transferService;
        this.loanService = loanService;
        this.tokenService = tokenService;
    }

    @PostMapping(path = "login")
    public ResponseEntity<String> login(@RequestParam String login, @RequestParam String password){
        int result = userService.login(login, password);
        switch(result){
            case 0: // Poprawny login i haslo
                User user = userService.getUserByLogin(login);
                String token = tokenService.newToken(user.getUser_id());
                return new ResponseEntity<>("Token: " + token + "\n" + user.toString(), HttpStatus.OK);

            case 1: // Niepoprawny login
                //throw new IllegalStateException("Incorrect login!");
                return new ResponseEntity<>("Incorrect login!", HttpStatus.NOT_FOUND);

            case 2: // Niepoprawne hasło
                //throw new IllegalStateException("Incorrect Password for user " + login);
                return new ResponseEntity<>("Incorrect Password for user " + login, HttpStatus.EXPECTATION_FAILED);

            case 3: // Nieaktywny uzytkownik
                //throw new IllegalStateException("User inactive!");
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
                //throw new IllegalStateException("Incorrect login!");
                return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);

            case 2: // Juz aktywowany
                //throw new IllegalStateException("User " + login + " is already activated!");
                return new ResponseEntity<>("User " + login + " is already activated!", HttpStatus.METHOD_NOT_ALLOWED);

            case 3: // Zły kod
                //throw new IllegalStateException("Incorrect activation code!");
                return new ResponseEntity<>("Incorrect activation code!", HttpStatus.EXPECTATION_FAILED);

            default: // Inne
                throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
        int result = userService.addNewUser(user);
        switch (result) {
            case 0: // Poprawna rejestracja
                return new ResponseEntity<>("User registered!", HttpStatus.OK);

            case 1: // Login juz istnieje
                return new ResponseEntity<>("User with that login already exist!", HttpStatus.EXPECTATION_FAILED);

            default: // Inne
                throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "updateUser")
    public ResponseEntity<String> updateUser(@RequestParam String tokenStr, Long id, String name, String surname, String login, String password_hash){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        int result;
        if(!userService.isAdmin(userId))
             result = userService.updateUser(userId, name, surname, login, password_hash);
        else if(id != null) result = userService.updateUser(id, name, surname, login, password_hash);
        else result = userService.updateUser(userId, name, surname, login, password_hash);
        switch(result){
            case 0:
                return new ResponseEntity<>("User updated successfully", HttpStatus.OK );
            case 1:
                return new ResponseEntity<>("User with that id does not exist!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Name too short!", HttpStatus.EXPECTATION_FAILED);
            case 3:
                return new ResponseEntity<>("Surname too short!", HttpStatus.EXPECTATION_FAILED);
            case 4:
                return new ResponseEntity<>("That login is already taken!", HttpStatus.CONFLICT);
            default:
                throw new IllegalStateException("Unknown error!");
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
                throw new IllegalStateException("Unknown error!");
        }
    }

    @GetMapping(path = "getAccounts")
    public ResponseEntity<String> getAccounts(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        List<Long> accountIds = accountService.getAccountsId(userId);
        if(accountIds.isEmpty()) return new ResponseEntity<>("No accounts found for this user!", HttpStatus.EXPECTATION_FAILED);
        else return new ResponseEntity<>(accountIds.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "createAccount")
    public ResponseEntity<String> createAccount(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

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
                throw new IllegalStateException("Unknown error!");
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
                throw new IllegalStateException("Unknown error!");
        }
    }

    @GetMapping(path = "getCurrencies")
    private ResponseEntity<String> getCurrencies(){
        return new ResponseEntity<>(currencyService.getCurrencies().toString(), HttpStatus.OK);
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
                throw new IllegalStateException("Unknown error!");
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
                throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "updateCurrency")
    private ResponseEntity<String> updateCurrency(@RequestParam String tokenStr, @RequestParam Long id, String name, Double sellVal, Double buyVal){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to update currencies!", HttpStatus.UNAUTHORIZED);

        int result = currencyService.updateCurrency(id, name, sellVal, buyVal);
        switch(result){
            case 0:
                return new ResponseEntity<>("Currency updated successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Currency with that id does not exist!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Currency with that name already exist!", HttpStatus.FOUND);
            case 3:
                return new ResponseEntity<>("Value is too small!", HttpStatus.EXPECTATION_FAILED);
            default:
                throw new IllegalStateException("Unknown error!");
        }
    }

    @PostMapping(path = "sendTransfer")
    private ResponseEntity<String> sendTransfer(@RequestParam String tokenStr, @RequestParam Long sender_account_id, @RequestParam Long receiver_account_id, @RequestParam Long currency_id, @RequestParam Double amount){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        Account acc = accountService.getAccountByID(sender_account_id);
        if(acc == null) return new ResponseEntity<>("That account does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!acc.getUser_id().equals(userId))
            return new ResponseEntity<>("This is not your account!", HttpStatus.UNAUTHORIZED);

        int result = transferService.makeTransfer(sender_account_id, receiver_account_id, currency_id, amount, LocalDate.now());
        switch(result){
            case 0:
                return new ResponseEntity<>("Transfer sent successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Incorrect sender ID!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Incorrect receiver ID!", HttpStatus.NOT_FOUND);
            case 3:
                return new ResponseEntity<>("Incorrect transfer value!", HttpStatus.METHOD_NOT_ALLOWED);
            case 4:
                return new ResponseEntity<>("Currency id is incorrect!", HttpStatus.FORBIDDEN);
            case 5:
                return new ResponseEntity<>("Transfer value cant be higher than account balance!", HttpStatus.NOT_ACCEPTABLE);
            case 6:
                return new ResponseEntity<>("Transfer added to pending list!", HttpStatus.ACCEPTED);
            default:
                throw new IllegalStateException("Unknown error!");
        }
    }

    @GetMapping(path = "getPendingTransfers")
    private ResponseEntity<String> getPendingTransfers(@RequestParam String tokenStr){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to get pending transfers!", HttpStatus.UNAUTHORIZED);

        List<String> result = new ArrayList<>();
        List<Pending_Transfer> pending_transfers = transferService.getPendingTransfers();
        for(Pending_Transfer pt : pending_transfers){
            String text = pt.toString() + "\n";
            Transfer_Info ti = transferService.getInfoById(pt.getTransfer_info_id());
            text += ti.toString();
            result.add(text);
        }

        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "acceptTransfer")
    private ResponseEntity<String> acceptTransfer(@RequestParam String tokenStr, @RequestParam Long pending_transfer_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to accept pending transfers!", HttpStatus.UNAUTHORIZED);

        int result = transferService.acceptTransfer(pending_transfer_id, userId);
        switch(result){
            case 0:
                return new ResponseEntity<>("Transfer sent successfully!", HttpStatus.OK);
            case 1:
                return new ResponseEntity<>("Incorrect sender ID!", HttpStatus.NOT_FOUND);
            case 2:
                return new ResponseEntity<>("Incorrect receiver ID!", HttpStatus.NOT_FOUND);
            case 3:
                return new ResponseEntity<>("Incorrect transfer value!", HttpStatus.METHOD_NOT_ALLOWED);
            case 4:
                return new ResponseEntity<>("Currency id is incorrect!", HttpStatus.FORBIDDEN);
            case 5:
                return new ResponseEntity<>("Transfer value cant be higher than account balance!", HttpStatus.NOT_ACCEPTABLE);
            case 6:
                return new ResponseEntity<>("Transfer added to pending list!", HttpStatus.ACCEPTED);
            case 7:
                return new ResponseEntity<>("Incorrect pending transfer id!", HttpStatus.EXPECTATION_FAILED);
            default:
                throw new IllegalStateException("Unknown error!");
        }
    }

    @DeleteMapping(path = "deleteTransfer")
    private ResponseEntity<String> deleteTransfer(@RequestParam String tokenStr, @RequestParam Long pending_transfer_id){
        Long userId = tokenService.getUserIdFromToken(tokenStr);

        if(userId == null)
            return new ResponseEntity<>("That token does not exist!", HttpStatus.EXPECTATION_FAILED);

        if(!userService.isAdmin(userId))
            return new ResponseEntity<>("This user is not authorized to delete pending transfers!", HttpStatus.UNAUTHORIZED);

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
        accountService.giveMoney(id);
    }

    @GetMapping(path = "admin")
    private void admin(@RequestParam Long id){
        userService.setAdmin(id);
    }

}
