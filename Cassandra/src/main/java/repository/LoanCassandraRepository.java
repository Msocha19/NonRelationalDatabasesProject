package repository;

import com.datastax.oss.driver.api.core.CqlSession;
import model.actions.Loan;
import model.dao.LoanDao;
import model.mappers.LoanMapper;
import model.mappers.LoanMapperBuilder;
import java.time.LocalDate;
import java.util.List;

public class LoanCassandraRepository {

    private LoanDao loanDao;

    public LoanCassandraRepository(CqlSession session) {
        LoanMapper loanMapper = new LoanMapperBuilder(session).build();
        this.loanDao = loanMapper.loanDao();
    }

    public void createLoan(int loandId, LocalDate beginDate, LocalDate endDate, int account, double initAmount, double paidAmount, double percentage, boolean archive){
        this.loanDao.create(new Loan(loandId, beginDate, endDate, account, initAmount, paidAmount, percentage, archive));
    }

    public Loan getLoan(int id) {
        return this.loanDao.findById(id);
    }

    public Loan deleteLoan(int id) {
        Loan loan = this.loanDao.findById(id);
        this.loanDao.delete(loan);
        return loan;
    }

    public List<Loan> getAll(Boolean archive) {
        if (archive == null) {
            return this.loanDao.getAll();
        }
        return this.loanDao.getAllArchivedOrNot(archive);
    }

    public void update(Loan loan) {
        this.loanDao.update(loan);
    }

//    public List<Loan> getLoansFromRange(LocalDate begin, LocalDate end) {
//        return this.loanDao.getLoansFromRange(begin, end);
//    }

    public Loan findLoanByAccount(int accountID) {
        return this.loanDao.findLoanByAccount(accountID);
    }

    public Integer findLastId() {
        return this.loanDao.findLastId();
    }

    public Boolean doesContain(int id) {
        return this.loanDao.doesContain(id);
    }
}
