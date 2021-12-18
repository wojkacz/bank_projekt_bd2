package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Loan_InfoRepository extends JpaRepository<Loan_Info, Long> {
}
