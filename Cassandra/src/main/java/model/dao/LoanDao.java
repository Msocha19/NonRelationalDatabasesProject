package model.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import model.Company;
import model.Person;
import model.actions.Loan;
import model.providers.ClientQueryProvider;
import model.providers.LoanQueryProvider;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface LoanDao {

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
    Loan findById(int id);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void create(Loan loan);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void delete(Loan loan);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
    List<Loan> getAll();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
    List<Loan> getAllArchivedOrNot(Boolean archived);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Update
    void update(Loan loan);

//    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
//    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
//    List<Loan> getLoansFromRange(LocalDate begin, LocalDate end);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
    Loan findLoanByAccount(int accountID);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
    Integer findLastId();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = LoanQueryProvider.class, entityHelpers = {Loan.class})
    Boolean doesContain(int id);
}
