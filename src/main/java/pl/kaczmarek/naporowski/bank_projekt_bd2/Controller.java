package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.LoanService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Token.TokenService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.TransferService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserService;

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


}
