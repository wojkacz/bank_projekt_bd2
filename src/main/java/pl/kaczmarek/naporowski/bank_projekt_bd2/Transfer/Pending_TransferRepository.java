package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kaczmarek.naporowski.bank_projekt_bd2.User.User;

import java.util.Optional;

@Repository
public interface Pending_TransferRepository extends JpaRepository<Pending_Transfer, Long> {

    @Query("SELECT pt FROM Pending_Transfer pt WHERE pt.pending_transfer_id = ?1")
    Optional<Pending_Transfer> findPendingTransferByID(Long id);
}
