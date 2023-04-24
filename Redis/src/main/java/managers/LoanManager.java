package managers;

import actions.Loan;
import model.Account;
import repository.LoanRepository;
import repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

public class LoanManager {

    private final Repository<Loan> repository;

    public LoanManager() {
        this.repository = new LoanRepository();
    }

    public void createLoan(double initAmount, double percentage, LocalDateTime endDate, Account account) throws Exception {
        try {
            if (endDate.isBefore(LocalDateTime.now())) {
                throw new Exception("Can not set date to past!");
            }
            Loan loan = new Loan(initAmount, percentage, endDate, account);
            loan.setLoanId(this.findLastId() + 1);
            if (this.contains(loan)) {
                throw new Exception("Loan with this id already exists!");
            } else {
                this.repository.add(loan);
                AccountManager accountManager = new AccountManager();
                accountManager.deposit(this.repository.find("_loanId", this.findLastId()).getInitAmount(), this.findLastId());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void update(Object id, Object field, Object newValue) throws Exception {
        Loan loan = repository.find("_loanId", id);
        if (this.contains(loan)) {
            repository.update("_loanId", field, newValue, id);
        } else {
            throw new Exception("This loan does not exist!");
        }
    }

    public Account findAccount(Integer id) {
        return (Account) this.repository.findNested(this.find(id));
    }

    public void archiviseLoan(Integer id) throws Exception {
        Loan loan = repository.find("_loanId", id);
        if (this.contains(loan)) {
            repository.update("_loanId", "archive", true, id);
        } else {
            throw new Exception("This loan does not exist!");
        }
    }

    public List<Loan> findAll() {
        return this.repository.getAll();
    }

    public Loan find(Integer id) {
        return this.repository.find("_loanId", id);
    }

    public int findLastId() {
        if (this.findAll().isEmpty()) {
            return 0;
        } else {
            return this.findAll().get(this.findAll().size() - 1).getLoanId();
        }
    }

    public boolean contains(Loan loan) {
        return this.findAll().contains(loan);
    }

    public void deleteAll() {
        this.repository.wipeAll();
    }
}
