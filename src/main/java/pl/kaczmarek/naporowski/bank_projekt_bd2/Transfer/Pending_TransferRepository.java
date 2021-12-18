package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Pending_TransferRepository extends JpaRepository<Pending_Transfer, Long> {
}
