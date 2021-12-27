package pl.kaczmarek.naporowski.bank_projekt_bd2.Code;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CodeRepository  extends JpaRepository<Code, Long> {
    @Query("SELECT c FROM Code c WHERE c.code = ?1")
    Optional<Code> findByCode(String code);
}
