package pl.kaczmarek.naporowski.bank_projekt_bd2.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Code.CodeService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Email.EmailService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class UserService {
    UserRepository userRepository;
    CodeService codeService;
    EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, CodeService codeService, EmailService emailService) { this.emailService = emailService; this.userRepository = userRepository; this.codeService = codeService; }

    public int addNewUser(String name, String surname, String login, String password) {
        if(userRepository.findUserByLogin(login).isPresent())
            return 1; // Istnieje już taki login

        User user = new User(name, surname, login, password);
        user = userRepository.save(user);
        codeService.sendActivationCode(user.getUser_id(), user.getLogin());
        return 0;
    }

    public int forgetPasswordSendCode(String login){
        Optional<User> userOptional = userRepository.findUserByLogin(login);

        if(userOptional.isEmpty())
            return 1; // Niepoprawny login

        codeService.sendVerificationCode(userOptional.get().getUser_id(), userOptional.get().getLogin());
        return 0;
    }

    public int forgetPasswordVerifyCode(String login, String code){
        Optional<User> userOptional = userRepository.findUserByLogin(login);

        if(userOptional.isEmpty())
            return 1; // Niepoprawny login
        User user = userOptional.get();

        if(code.length() != 4 || !codeService.checkVerificationCode(user.getUser_id(), code)) return 2; // Niepoprawny kod

        String password = generateRandomPassword();
        String passwordHash = getHash(password);
        emailService.send(login, "Password changed!\nYour new password is: " + password);
        user.setPassword_hash(passwordHash);
        userRepository.save(user);
        return 0;
    }

    private String generateRandomPassword(){
        String chars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        String result = "";
        Random random = new Random();
        for(int i = 0; i < 12; i++) {
            int rand = random.nextInt(chars.length());
            result += chars.charAt(rand);
        }
        return result;
    }

    private String getHash(String text){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[MessageDigest]" + e.getMessage());
        }
        assert md != null;
        md.update(text.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for(byte b : digest)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public int activateUser(String login, String code){
        Optional<User> userOptional = userRepository.findUserByLogin(login);

        if(userOptional.isEmpty())
            return 1; // Niepoprawny login

        User user = userOptional.get();
        if(user.isActivated())
            return 2; // Już aktywowany

        if(!codeService.checkActivationCode(user.getUser_id(), code))
            return 3; // Niepoprawny kod

        user.setActivated(true);
        userRepository.save(user);
        return 0;
    }

    public int login(String login, String password) {
        Optional<User> userOptional = userRepository.findUserByLogin(login);

        if(userOptional.isEmpty())
            return 1; // Nie istnieje

        User user = userOptional.get();
        if(!user.getPassword_hash().equals(password))
            return 2; // Niepoprawne hasło

        if(!user.isActivated())
            return 3; // Nie aktywowany

        return 0; // Pomyslnie
    }

    public User getUserByLogin(String login) {
        Optional<User> userOptional = userRepository.findUserByLogin(login);
        return userOptional.orElse(null);
    }

    public boolean isAdmin(Long user_id){
        Optional<User> userOptional = userRepository.findUserByID(user_id);
        if(userOptional.isEmpty()) return false;

        return userOptional.get().getPermission_level() > 0;
    }

    public int updateUser(Long user_id, String password_hash, String name, String surname, String login, String new_password_hash){
        Optional<User> userOptional = userRepository.findUserByID(user_id);
        if(userOptional.isEmpty()) return 1;

        User user = userOptional.get();
        if(!user.getPassword_hash().equals(password_hash))
            return 2; // Niepoprawne hasło

        if(name != null) {
            if(name.length() <= 1)
                return 3; // Za krotkie imie
            user.setName(name);
        }

        if(surname != null) {
            if(surname.length() <= 1)
                return 4; // Za krotkie nazwisko
            user.setSurname(surname);
        }

        if(login != null){
            if(login.length() <= 1)
                return 5; // Za krótki login

            if(userRepository.findUserByLogin(login).isPresent())
                return 6; // Istnieje juz taki login

            user.setLogin(login);
        }

        if(new_password_hash != null){
            if(new_password_hash.length() <= 1)
                return 7; // Za krotki hasz
            user.setPassword_hash(new_password_hash);
        }

        userRepository.save(user);
        return 0; // pomyslnie
    }

    public void setAdmin(Long id){
        Optional<User> userOptional = userRepository.findUserByID(id);
        if(userOptional.isEmpty()) return;

        User user = userOptional.get();
        user.setPermission_level(1);
        userRepository.save(user);
    }
}
