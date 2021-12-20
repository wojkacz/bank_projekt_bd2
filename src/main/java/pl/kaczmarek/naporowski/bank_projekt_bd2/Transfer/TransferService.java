package pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    Pending_TransferRepository pendingTransferRepository;
    TransferRepository transferRepository;
    Transfer_InfoRepository transferInfoRepository;

    @Autowired
    public TransferService(Pending_TransferRepository pendingTransferRepository, TransferRepository transferRepository, Transfer_InfoRepository transferInfoRepository) {
        this.pendingTransferRepository = pendingTransferRepository;
        this.transferRepository = transferRepository;
        this.transferInfoRepository = transferInfoRepository;
    }
}
