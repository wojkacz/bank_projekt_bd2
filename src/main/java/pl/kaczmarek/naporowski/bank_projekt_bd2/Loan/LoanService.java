package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    LoanRepository loanRepository;
    Loan_InfoRepository loanInfoRepository;
    Pending_LoanRepository pendingLoanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, Loan_InfoRepository loanInfoRepository, Pending_LoanRepository pendingLoanRepository) {
        this.loanRepository = loanRepository;
        this.loanInfoRepository = loanInfoRepository;
        this.pendingLoanRepository = pendingLoanRepository;
    }
}
