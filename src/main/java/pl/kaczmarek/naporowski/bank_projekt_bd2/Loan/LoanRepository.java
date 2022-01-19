package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;

import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {


}
