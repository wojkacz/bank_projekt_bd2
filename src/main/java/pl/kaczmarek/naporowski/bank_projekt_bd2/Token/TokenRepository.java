package pl.kaczmarek.naporowski.bank_projekt_bd2.Token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.token = ?1")
    Optional<Token> findTokenByStr(String tokenStr);

    @Query("SELECT t FROM Token t WHERE t.user_id = ?1")
    Optional<Token> findTokenByUserID(Long user_id);
}
