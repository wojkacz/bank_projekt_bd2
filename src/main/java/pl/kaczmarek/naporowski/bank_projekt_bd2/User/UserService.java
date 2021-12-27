package pl.kaczmarek.naporowski.bank_projekt_bd2.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Code.CodeService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Email.EmailService;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class UserService {
    UserRepository userRepository;
    CodeService codeService;

    @Autowired
    public UserService(UserRepository userRepository, CodeService codeService) { this.userRepository = userRepository; this.codeService = codeService; }

    public int addNewUser(String name, String surname, String login, String password) {
        if(userRepository.findUserByLogin(login).isPresent())
            return 1; // Istnieje już taki login

        User user = new User(name, surname, login, password);
        user = userRepository.save(user);
        codeService.sendActivationCode(user.getUser_id(), user.getLogin());
        return 0;
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

    public int updateUser(Long user_id, String name, String surname, String login, String password_hash){
        Optional<User> userOptional = userRepository.findUserByID(user_id);
        if(userOptional.isEmpty()) return 1;

        User user = userOptional.get();

        if(name != null) {
            if(name.length() <= 1)
                return 2; // Za krotkie imie
            user.setName(name);
        }

        if(surname != null) {
            if(surname.length() <= 1)
                return 3; // Za krotkie nazwisko
            user.setSurname(surname);
        }

        if(login != null){
            if(login.length() <= 1)
                return 4; // Za krótki login

            if(userRepository.findUserByLogin(login).isPresent())
                return 5; // Istnieje juz taki login

            user.setLogin(login);
        }

        if(password_hash != null){
            if(password_hash.length() <= 1)
                return 6; // Za krotki hasz
            user.setPassword_hash(password_hash);
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
