package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Pending_LoanRepository extends JpaRepository<Pending_Loan, Long> {
}
