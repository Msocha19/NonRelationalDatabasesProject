package managers;

import com.datastax.oss.driver.api.core.CqlSession;
import model.Account;
import model.actions.Loan;
import repository.AccountCassandraRepository;
import repository.LoanCassandraRepository;

import java.time.LocalDate;
import java.util.List;

public class LoanManager {

    private LoanCassandraRepository loanCassandraRepository;
    private AccountCassandraRepository accountCassandraRepository;

    public LoanManager(CqlSession session) {
        this.loanCassandraRepository = new LoanCassandraRepository(session);
        this.accountCassandraRepository = new AccountCassandraRepository(session);
    }

    public void create(LocalDate endDate, Integer account, Double initAmount, Double percentage) {
        if (!endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past!");
        }
        if (!this.accountCassandraRepository.doesItContain(account)) {
            throw new IllegalArgumentException("Konto o podanym ID nie istnieje");
        }
        if (initAmount <= 0) {
            throw new IllegalArgumentException("Initial amount have to be grater than 0!");
        }
        if (percentage <= 0) {
            throw new IllegalArgumentException("Percentage have to be grater than 0!");
        }
        Account acc = this.accountCassandraRepository.getAccount(account);
        if (!acc.isActive()) {
            throw new IllegalArgumentException("Cannot take loan for inactive account!");
        }
        this.loanCassandraRepository.createLoan(this.loanCassandraRepository.findLastId() + 1, LocalDate.now(), endDate, account, initAmount, 0, percentage, false);
        acc.deposit(initAmount);
        this.accountCassandraRepository.update(acc);
    }

    public void delete(int loanId) throws Exception {
        Loan loan = this.loanCassandraRepository.deleteLoan(loanId);
        if (this.accountCassandraRepository.doesItContain(loan.getAccount())) {
            Account account = this.accountCassandraRepository.getAccount(loan.getAccount());
            account.withdraw(loan.getInitAmount() - loan.getPaidAmount());
            this.accountCassandraRepository.update(account);
        }
    }

    public void update(Integer loanId, Double initAmount, Double paidAmount, Double percentage, Boolean archive) {
        Loan loan = this.loanCassandraRepository.getLoan(loanId);
        if (initAmount > 0) {
            loan.setInitAmount(initAmount);
        }
        if (paidAmount >= 0) {
            loan.setPaidAmount(paidAmount);
        }
        if (percentage > 0) {
            loan.setPercentage(percentage);
        }
        if (archive != null) {
            loan.setArchive(archive);
        }
        this.loanCassandraRepository.update(loan);
    }

    public void update(Loan loan) {
        this.loanCassandraRepository.update(loan);
    }

    public List<Loan> getAll(Boolean archive) {
        return this.loanCassandraRepository.getAll(archive);
    }

    public Loan getLoan(int id) {
        return this.loanCassandraRepository.getLoan(id);
    }

//    public List<Loan> getLoansFromRange(LocalDate begin, LocalDate end) {
//        if (!end.isAfter(begin)) {
//            throw new IllegalArgumentException("Given range is wrong !");
//        }
//        return this.loanCassandraRepository.getLoansFromRange(begin, end);
//    }

    public Loan findLoanByAccount(int accountID) {
        if (!this.accountCassandraRepository.doesItContain(accountID)) {
            throw new IllegalArgumentException("Account does not exist!");
        }
        return this.loanCassandraRepository.findLoanByAccount(accountID);
    }

    public void setArchive(int loanID) {
        Loan loan = this.loanCassandraRepository.getLoan(loanID);
        loan.setArchive(true);
        this.loanCassandraRepository.update(loan);
    }
}
