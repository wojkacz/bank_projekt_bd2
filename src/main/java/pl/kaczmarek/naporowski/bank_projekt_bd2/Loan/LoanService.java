package pl.kaczmarek.naporowski.bank_projekt_bd2.Loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.Account;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Account.AccountService;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Pending_Transfer;
import pl.kaczmarek.naporowski.bank_projekt_bd2.Transfer.Transfer_Info;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    LoanRepository loanRepository;
    Loan_InfoRepository loanInfoRepository;
    Pending_LoanRepository pendingLoanRepository;
    AccountService accountService;

    @Autowired
    public LoanService(LoanRepository loanRepository, Loan_InfoRepository loanInfoRepository, Pending_LoanRepository pendingLoanRepository, AccountService accountService) {
        this.loanRepository = loanRepository;
        this.loanInfoRepository = loanInfoRepository;
        this.pendingLoanRepository = pendingLoanRepository;
        this.accountService = accountService;
    }

    public int takeLoan(Long account_id, Double amount, int loan_length, LocalDate date){
        Account acc = accountService.getAccountByID(account_id);
        if(acc == null) return 1; // Konto nie istnieje
        if(amount < 1000.0) return 2; // Kwota zbyt niska
        if(loan_length < 1) return 3; // Za krotki czas pozyczki

        List<Loan_Info> loanInfoList = loanInfoRepository.findAll();
        for(Loan_Info li : loanInfoList){
            if(li.getAccount_id().equals(account_id))
                return 4; // Juz ma pozyczke lub zlozyl prosbe
        }

        Loan_Info loan_info = new Loan_Info(account_id, amount, loan_length, date);
        loan_info = loanInfoRepository.save(loan_info);

        Pending_Loan pending_loan = new Pending_Loan(loan_info.getLoan_info_id());
        pendingLoanRepository.save(pending_loan);
        return 0;
    }

    @Transactional
    public int acceptPendingLoan(Long pending_loan_id, Long employee_id){
        if(!pendingLoanRepository.existsById(pending_loan_id)) return 1; // Zle id
        Pending_Loan pending_loan = pendingLoanRepository.getById(pending_loan_id);

        if(!loanInfoRepository.existsById(pending_loan.getLoan_info_id()))
            return 2; // Nie znaleziono informacji
        Loan_Info loan_info = loanInfoRepository.getById(pending_loan.getLoan_info_id());

        if(accountService.addBalanceToAccount(loan_info.getAccount_id(), loan_info.getAmount()) != 0)
            return 3; // Nie znaleziono konta

        pendingLoanRepository.delete(pending_loan);
        loanRepository.save(new Loan(loan_info.getLoan_info_id(), employee_id));
        return 0;
    }

    public int deletePendingLoan(Long pending_loan_id){
        if(!pendingLoanRepository.existsById(pending_loan_id)) return 1; // Zle id
        Pending_Loan pending_loan = pendingLoanRepository.getById(pending_loan_id);

        if(!loanInfoRepository.existsById(pending_loan.getLoan_info_id()))
            return 2; // Nie znaleziono informacji
        Loan_Info loan_info = loanInfoRepository.getById(pending_loan.getLoan_info_id());

        loanInfoRepository.delete(loan_info);
        pendingLoanRepository.delete(pending_loan);

        return 0;
    }

    public Loan_Info getInfoByPendingID(Long id){
        if(!pendingLoanRepository.existsById(id)) return null;

        Pending_Loan pl = pendingLoanRepository.getById(id);
        return loanInfoRepository.getById(pl.getLoan_info_id());
    }

    public Loan_Info getInfoByLoanID(Long id){
        if(!loanRepository.existsById(id)) return null;

        Loan l = loanRepository.getById(id);
        return loanInfoRepository.getById(l.getLoan_info_id());
    }

    public List<Pending_Loan> getPendingLoans(){
        return pendingLoanRepository.findAll();
    }

    @Transactional
    public int payInstallement(Long loan_id){
        if(!loanRepository.existsById(loan_id)) return 1; // Zle id
        Loan loan = loanRepository.getById(loan_id);

        if(!loanInfoRepository.existsById(loan.getLoan_info_id()))
            return 2; // Nie znaleziono informacji
        Loan_Info loan_info = loanInfoRepository.getById(loan.getLoan_info_id());

        Double amount = getLoanInstallementValue(loan_info.getLoan_info_id());
        if(amount == null) return 6; // Nie pobrano wartosci
        int result = accountService.payInstallement(loan_info.getAccount_id(), amount);
        switch(result){
            case 0:
                loan.setPaid_installments(loan.getPaid_installments() + 1);
                if(loan.getPaid_installments() == loan_info.getLoan_length()){
                    loanInfoRepository.delete(loan_info);
                    loanRepository.delete(loan);
                    return 7; // Splacona
                }
                loanRepository.saveAndFlush(loan);
                return 0;
            case 1:
                return 3; // Konto nie istnieje
            case 2:
                return 4; // Nie ma wystarczajaco na koncie
            default:
                return 5; // Inne
        }
    }

    public Double getLoanInstallementValue(Long loan_info_id){
        if(!loanInfoRepository.existsById(loan_info_id))
            return null;
        Loan_Info loan_info = loanInfoRepository.getById(loan_info_id);
        return ((loan_info.getAmount() * 1.1) / loan_info.getLoan_length());
    }

    public Loan getLoanByAccountID(Long account_id){
        List<Loan> loans = loanRepository.findAll();
        for(Loan loan : loans){
            Loan_Info li = loanInfoRepository.getById(loan.getLoan_info_id());
            if(li.getAccount_id().equals(account_id))
                return loan;
        }
        return null;
    }
}
