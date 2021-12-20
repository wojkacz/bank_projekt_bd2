package pl.kaczmarek.naporowski.bank_projekt_bd2.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public int addNewUser(User user) {
        Optional<User> userOptional = userRepository.findUserByLogin(user.getLogin());
        if(userOptional.isPresent()) {
            System.out.println("User Login already taken!");
            return 1;
        }

        Random random = new Random();
        StringBuilder actCode = new StringBuilder();
        for(int i = 0; i < 4; i++)
            actCode.append(random.nextInt(10));

        user.setActivationCode(actCode.toString());
        // SEND EMAIL WITH CODE
        userRepository.save(user);

        return 0;
    }

    public int activateUser(String login, String code){
        Optional<User> userOptional = userRepository.findUserByLogin(login);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(!user.isActivated()){
                if(user.getActivationCode().equals(code)){
                    user.setActivated(true);
                    user.setActivationCode(null);
                    userRepository.save(user);
                    return 0;
                }
                System.out.println("Wrong activation code!");
                return 3;
            }
            System.out.println("User with that login is already activated!");
            return 2;
        }
        System.out.println("User with that login does not exist!");
        return 1;
    }

    public int login(String login, String password) {
        Optional<User> userOptional = userRepository.findUserByLogin(login);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(user.getPassword_hash().equals(password)){
                if(user.isActivated()) return 0;
                System.out.println("User not activated!");
                return 3;
            }
            System.out.println("Incorrect password!");
            return 2;
        }
        System.out.println("User with that login does not exist!");
        return 1;
    }

    public User getUserByLogin(String login) {
        Optional<User> userOptional = userRepository.findUserByLogin(login);
        return userOptional.orElse(null);
    }

    public boolean isAdmin(Long user_id){
        if(userRepository.existsById(user_id)) {
            User user = userRepository.getById(user_id);
            return user.getPermission_level() > 0;
        }
        return false;
    }
}
