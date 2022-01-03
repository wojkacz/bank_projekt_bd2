package pl.kaczmarek.naporowski.bank_projekt_bd2.Code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Email.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CodeService {
    CodeRepository codeRepository;
    EmailService emailService;

    @Autowired
    public CodeService(CodeRepository codeRepository, EmailService emailService){
        this.codeRepository = codeRepository;
        this.emailService = emailService;
    }

    public void sendActivationCode(Long user_id, String email){
        String codeStr;
        do {
            codeStr = generateCode();
        } while(codeRepository.findByCode(codeStr).isPresent());
        Code code = new Code(user_id, 1, codeStr);

        emailService.send(email, "Account activation\nActivation Code is: " + codeStr + "\nCode expires in 15 minutes.");
        codeRepository.save(code);
    }

    public void sendVerificationCode(Long user_id, String email){
        String codeStr;
        do {
            codeStr = generateCode();
        } while(codeRepository.findByCode(codeStr).isPresent());
        Code code = new Code(user_id, 2, codeStr);
        emailService.send(email, "Forget password verification\nVerification Code is: " + codeStr + "\nCode expires in 15 minutes.");
        codeRepository.save(code);
    }

    public boolean checkVerificationCode(Long user_id, String code){
        Optional<Code> codeOptional = codeRepository.findByCode(code);
        if(codeOptional.isEmpty())
            return false;

        if(!codeOptional.get().getUser_id().equals(user_id))
            return false;

        if(codeOptional.get().getType() == 2){
            codeRepository.delete(codeOptional.get());
            return true;
        }
        return false;
    }

    public boolean checkActivationCode(Long user_id, String code){
        Optional<Code> codeOptional = codeRepository.findByCode(code);
        if(codeOptional.isEmpty())
            return false;

        if(!codeOptional.get().getUser_id().equals(user_id))
            return false;

        if(codeOptional.get().getType() == 1){
            codeRepository.delete(codeOptional.get());
            return true;
        }
        return false;
    }

    public void verifyCodes(){
        List<Code> codes = codeRepository.findAll();
        for(Code code : codes)
            if(LocalDateTime.now().isAfter(code.getExpirationTime()))
                codeRepository.delete(code);
    }

    private String generateCode(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 4; i++)
            sb.append(random.nextInt(10));
        return sb.toString();
    }

    /*
    Random random = new Random();
    StringBuilder actCode = new StringBuilder();

    for(int i = 0; i < 4; i++)
        actCode.append(random.nextInt(10));

    user.setActivationCode(actCode.toString());
    emailService.send(user.getLogin(), "Hi " + name + "!\nVerification Code is: " + actCode);
     */
}
