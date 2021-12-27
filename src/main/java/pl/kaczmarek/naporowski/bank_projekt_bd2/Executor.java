package pl.kaczmarek.naporowski.bank_projekt_bd2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Code.VerifyCodeThread;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Token.VerifyTokenThread;

@Component
public class Executor {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void atStartup() {
        VerifyTokenThread verifyTokenThread = applicationContext.getBean(VerifyTokenThread.class);
        VerifyCodeThread verifyCodeThread = applicationContext.getBean(VerifyCodeThread.class);
        taskExecutor.execute(verifyTokenThread);
        taskExecutor.execute(verifyCodeThread);
        System.out.println("Threads created successfully!");
    }
}
