package pl.kaczmarek.naporowski.bank_projekt_bd2.Code;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
public class Code {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long code_id;
    private Long user_id;
    private LocalDateTime expirationTime;
    private int type;
    private String code;

    public Code(Long user_id, int type, String code) {
        this.user_id = user_id;
        this.expirationTime = LocalDateTime.now().plusMinutes(15);
        this.type = type;
        this.code = code;
    }

    public Code() {
    }

    public Long getCode_id() {
        return code_id;
    }

    public void setCode_id(Long code_id) {
        this.code_id = code_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
