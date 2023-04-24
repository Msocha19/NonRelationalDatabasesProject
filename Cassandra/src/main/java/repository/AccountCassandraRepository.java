package repository;

import com.datastax.oss.driver.api.core.CqlSession;
import model.Account;
import model.dao.AccountDao;
import model.mappers.AccountMapper;
import model.mappers.AccountMapperBuilder;

import java.util.List;

public class AccountCassandraRepository {

    private AccountDao accountDao;

    public AccountCassandraRepository(CqlSession session) {
        AccountMapper clientMapper = new AccountMapperBuilder(session).build();
        this.accountDao = clientMapper.accountDao();
    }

    public void createAccount(int number, String accountType, double percentage, int owner) throws Exception{
        Account account = new Account(number, percentage, accountType, owner);
        this.accountDao.create(account);
    }

    public Account getAccount(int number) {
        return this.accountDao.findByNumber(number);
    }

    public Account deleteAccount(int number) {
        Account account = this.getAccount(number);
        this.accountDao.delete(account);
        return account;

    }

    public List<Account> findAllAccountsForClient(int clientID) {
        return this.accountDao.findAllAccountsForClient(clientID);
    }

    public List<Account> getAccountsByType(String type) {
        return this.accountDao.findByType(type);
    }

    public List<Account> getAll() {
        return this.accountDao.getAll();
    }

    public Boolean doesItContain(int number) {
        return this.accountDao.doesContain(number);
    }

    public void update(Account account) {
        this.accountDao.update(account);
    }
}
