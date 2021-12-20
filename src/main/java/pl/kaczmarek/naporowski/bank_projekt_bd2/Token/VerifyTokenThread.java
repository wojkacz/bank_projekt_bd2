package pl.kaczmarek.naporowski.bank_projekt_bd2.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyTokenThread implements Runnable {

    @Autowired
    TokenService tokenService;

    @Override
    public void run() {
        while (true) {
            try {
                tokenService.verifyTokens();
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
