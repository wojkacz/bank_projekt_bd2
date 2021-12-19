package pl.kaczmarek.naporowski.bank_projekt_bd2.User;

import javax.persistence.*;

@Entity(name = "Users")
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long user_id;
    private String name;
    private String surname;
    private String login;
    private String password_hash;
    private int permission_level;

    private boolean isActivated;
    private String activationCode;

    public User(Long user_id, String name, String surname, String login, String password_hash, int permission_level) {
        this.user_id = user_id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password_hash = password_hash;
        this.permission_level = permission_level;
        isActivated = false;
    }

    public User(String name, String surname, String login, String password_hash) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password_hash = password_hash;
        permission_level = 0;
        isActivated = false;
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

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public int getPermission_level() {
        return permission_level;
    }

    public void setPermission_level(int permission_level) {
        this.permission_level = permission_level;
    }
}
