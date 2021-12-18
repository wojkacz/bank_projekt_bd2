package pl.kaczmarek.naporowski.bank_projekt_bd2.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long user_id;
    private String name;
    private String surname;
    private String login;
    private String password_hash;
    private Integer permission_level;

    public User(Long user_id, String name, String surname, String login, String password_hash, Integer permission_level) {
        this.user_id = user_id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password_hash = password_hash;
        this.permission_level = permission_level;
    }

    public User(String name, String surname, String login, String password_hash, Integer permission_level) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password_hash = password_hash;
        this.permission_level = permission_level;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", login='" + login + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", permission_level=" + permission_level +
                '}';
    }
}
