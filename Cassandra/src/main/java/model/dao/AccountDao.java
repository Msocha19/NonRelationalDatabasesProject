package model.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import model.Account;
import model.providers.AccountQueryProvider;

import java.util.List;

@Dao
public interface AccountDao {

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = AccountQueryProvider.class, entityHelpers = {Account.class})
    Account findByNumber(int number);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = AccountQueryProvider.class, entityHelpers = {Account.class})
    List<Account> findAllAccountsForClient(int clientID);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = AccountQueryProvider.class, entityHelpers = {Account.class})
    List<Account> getAll();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = AccountQueryProvider.class, entityHelpers = {Account.class})
    List<Account> findByType(String type);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = AccountQueryProvider.class, entityHelpers = {Account.class})
    Boolean doesContain(int number);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void create(Account account);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void delete(Account account);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Update
    void update(Account account);
}
