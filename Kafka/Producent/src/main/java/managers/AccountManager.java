package managers;

import model.Account;
import model.AccountType;
import model.ClientAddress;
import repository.AccountRepository;
import repository.Repository;

import java.util.List;

public class AccountManager {

    private final Repository<Account> repository;

    public AccountManager() {
        this.repository = new AccountRepository();
    }

    public void deposit(double value, Integer id) {
        Account account = this.find(id);
        account.deposit(value);
        this.repository.update("_number", "balance", account.getBalance(), id);
    }

    public void withdraw(double value, Integer id) throws Exception {
        Account account = this.repository.find("_number", id);
        account.withdraw(value);
        this.repository.update("_number", "balance", account.getBalance(), id);
    }

    public void createAccount(int number, double percentage, AccountType accountType, ClientAddress ca) throws Exception {
        Account account = new Account(number, percentage, accountType, ca);
        ca.openAccount(account);
        ClientManager clientManager = new ClientManager();

        if (this.contains(account)) {
            throw new Exception("Account with this id already exists!");
        }

        this.repository.add(account);
        ClientAddress clientAddress = clientManager.find(account.getOwner().getId());
        clientAddress.openAccount(account);
        clientManager.update(account.getOwner().getId(), "accounts", clientAddress.getAccounts());
        this.update(number, "client", clientManager.find(account.getOwner().getId()));
    }

    public void deleteAccount(Integer id) throws Exception {
        ClientManager clientManager = new ClientManager();
        Account account = repository.find("_number", id);

        if (!this.contains(account)) {
            throw new Exception("This account does not exist!");
        }

        ClientAddress clientAddress = clientManager.find(account.getOwner().getId());
        clientAddress.closeAccount(account);
        clientManager.update(account.getOwner().getId(), "accounts", clientAddress.getAccounts());
        repository.delete("_number", account.getNumber());
    }

    public List<Account> findAll() {
        return this.repository.getAll();
    }

    public Account find(Integer id) {
        return this.repository.find("_number", id);
    }

    public ClientAddress getClient(Integer id) {
        return (ClientAddress) this.repository.findNested(this.find(id));
    }

    public boolean contains(Account account) {
        return repository.getAll().contains(account);
    }

    public void deleteAll() {
        this.repository.wipeAll();
    }

    public void deleteMany(List<Integer> ids) {
        this.repository.deleteMany("_number", ids);
    }

    public void update(Object id, Object field, Object newValue) throws Exception {
        Account account = repository.find("_number", id);

        if (!this.contains(account)) {
            throw new Exception("This account doesn't exist!");
        }

        repository.update("_number", field, newValue, id);
        if (field.equals("_number")) {
            ClientManager clientManager = new ClientManager();
            clientManager.updateAccountsID(account.getOwner().getId(), account.getNumber(), (Integer) newValue);
            repository.update("_number", "client.accounts", clientManager.find(account.getOwner().getId()).getAccounts(), newValue);
        }
    }
}
