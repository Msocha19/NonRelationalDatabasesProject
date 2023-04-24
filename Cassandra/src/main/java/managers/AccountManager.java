package managers;

import com.datastax.oss.driver.api.core.CqlSession;
import jnr.ffi.annotations.In;
import model.Account;
import model.Client;
import repository.AccountCassandraRepository;
import repository.ClientCassandraRepository;
import java.util.List;

public class AccountManager {

    private AccountCassandraRepository accountCassandraRepository;
    private ClientCassandraRepository clientCassandraRepository;

    public AccountManager(CqlSession session) {
        this.accountCassandraRepository = new AccountCassandraRepository(session);
        this.clientCassandraRepository = new ClientCassandraRepository(session);
    }

    public void create(Account account) throws Exception {
        this.accountCassandraRepository.createAccount(account.getNumber(), account.getAccountType(), account.getPercentage(), account.getOwner());
    }

    public void create(Integer number, double percentage, String accountType, int owner) throws Exception {
        if(percentage < 0) {
            throw new Exception("Oprocentowanie nie może być ujemne!");
        }
        if(this.accountCassandraRepository.doesItContain(number)) {
            throw new Exception("Konto o takim numerze juz istnieje!");
        }
        if(!this.clientCassandraRepository.doesItContain(owner)) {
            throw new Exception("Klient o podanym id nie istnieje");
        }
        if(!accountType.equals("Normal") && !accountType.equals("Savings") || accountType == null || accountType.equals("")) {
            throw new Exception("Podano bledny typ konta!");
        }
        Client client = this.clientCassandraRepository.getClient(owner);
        List<Integer> accounts = client.getAccounts();
        if (accounts.contains(number)) {
            throw new Exception("Client already opened this Account!");
        }
        accounts.add(number);
        this.accountCassandraRepository.createAccount(number, accountType, percentage, owner);
        this.clientCassandraRepository.insertAccount(accounts, client);
    }

    public void delete(Integer number) {
        Account account = this.accountCassandraRepository.deleteAccount(number);
        Client client = this.clientCassandraRepository.getClient(account.getOwner());
        List<Integer> accounts = client.getAccounts();
        accounts.remove(number);
        this.clientCassandraRepository.insertAccount(accounts, client);
    }

    public List<Account> getAccountsByType(String type) {
        if (!type.equals("Normal") && !type.equals("Savings") || type == null || type.equals("")) {
            throw new IllegalArgumentException("Podano bledny typ konta!");
        }
        return this.accountCassandraRepository.getAccountsByType(type);
    }

    public List<Account> getAll() {
        return this.accountCassandraRepository.getAll();
    }

    public List<Account> findByClient(int clientID) {
        if(!this.clientCassandraRepository.doesItContain(clientID)) {
            throw new IllegalArgumentException("Klient o podanym id nie istnieje");
        }
        return this.accountCassandraRepository.findAllAccountsForClient(clientID);
    }

    public Client getClientByAccount(int accountID) {
        Account account = this.accountCassandraRepository.getAccount(accountID);
        return this.clientCassandraRepository.getClient(account.getOwner());
    }

    public Account getAccount(int number) {
        return this.accountCassandraRepository.getAccount(number);
    }

    public void updateAccount(Integer accountID, Double balance, Double percentage, Integer owner, Boolean active) {
        Account account = this.accountCassandraRepository.getAccount(accountID);
        if (owner > 0 && this.clientCassandraRepository.doesItContain(owner)) {
            Client oldClient = this.clientCassandraRepository.getClient(account.getOwner());
            Client newClient = this.clientCassandraRepository.getClient(owner);
            List<Integer> oldClientList = oldClient.getAccounts();
            List<Integer> newClientList = newClient.getAccounts();
            oldClientList.remove(accountID);
            newClientList.add(accountID);
            this.clientCassandraRepository.insertAccount(oldClientList, oldClient);
            this.clientCassandraRepository.insertAccount(newClientList, newClient);
            account.setOwner(owner);
        }
        if (balance >= 0) {
            account.setBalance(balance);
        }
        if (percentage > 0) {
            account.setPercentage(percentage);
        }
        if (active != null) {
            account.setActive(active);
        }
        this.accountCassandraRepository.update(account);
    }

    public void update(Account account) {
        this.accountCassandraRepository.update(account);
    }

    public void deposit(int accountId, double amount) {
        Account account = this.accountCassandraRepository.getAccount(accountId);
        account.deposit(amount);
        this.accountCassandraRepository.update(account);
    }

    public void withdraw(int accountId, double amount) throws Exception {
        Account account = this.accountCassandraRepository.getAccount(accountId);
        account.withdraw(amount);
        this.accountCassandraRepository.update(account);
    }
}
