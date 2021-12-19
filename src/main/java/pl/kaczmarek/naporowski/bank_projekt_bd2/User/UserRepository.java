package pl.kaczmarek.naporowski.bank_projekt_bd2.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM Users u WHERE u.login = ?1")
    Optional<User> findUserByLogin(String login);
}
