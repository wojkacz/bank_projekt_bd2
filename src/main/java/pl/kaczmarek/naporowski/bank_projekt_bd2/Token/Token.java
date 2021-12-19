package pl.kaczmarek.naporowski.bank_projekt_bd2.Token;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
public class Token {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long token_id;
    private Long user_id;
    private String token = "";
    private LocalDateTime expiration_time;

    public Token() {
    }

    public Token(Long user_id) {
        this.user_id = user_id;
        this.expiration_time = LocalDateTime.now().plusMinutes(15);
        this.token = "";
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration_time() {
        return expiration_time;
    }

    public void setExpiration_time(LocalDateTime expiration_time) {
        this.expiration_time = expiration_time;
    }
}
