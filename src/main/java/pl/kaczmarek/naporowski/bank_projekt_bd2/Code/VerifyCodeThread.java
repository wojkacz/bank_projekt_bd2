package pl.kaczmarek.naporowski.bank_projekt_bd2.Code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyCodeThread implements Runnable {

    @Autowired
    CodeService codeService;

    @Override
    public void run() {
        while (true) {
            try {
                codeService.verifyCodes();
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}