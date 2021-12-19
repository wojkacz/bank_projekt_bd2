package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Currency.CurrencyService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Loan.LoanService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Token.TokenService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.TransferService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.UserService;

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


}
