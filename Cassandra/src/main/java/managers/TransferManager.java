package managers;

import com.datastax.oss.driver.api.core.CqlSession;
import model.Account;
import model.actions.Transfer;
import repository.AccountCassandraRepository;
import repository.TransferCassandraRepository;

import java.time.LocalDate;
import java.util.List;

public class TransferManager {

    private TransferCassandraRepository transferCassandraRepository;
    private AccountCassandraRepository accountCassandraRepository;

    public TransferManager(CqlSession session) {
        this.transferCassandraRepository = new TransferCassandraRepository(session);
        this.accountCassandraRepository = new AccountCassandraRepository(session);
    }

    public void create(Integer fromAccount, Integer toAccount, Double amount, LocalDate transferDate) throws Exception {
        if (!this.accountCassandraRepository.doesItContain(fromAccount) || !this.accountCassandraRepository.doesItContain(toAccount)) {
            throw new IllegalArgumentException("There are not such accounts!");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0!");
        }
        if (transferDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot set transfer to past date!");
        }
        Account toAcc = this.accountCassandraRepository.getAccount(toAccount);
        Account fromAcc = this.accountCassandraRepository.getAccount(fromAccount);
        if (!toAcc.isActive() || !fromAcc.isActive()) {
            throw new IllegalArgumentException("Inactive accounts!");
        }
        if (fromAcc.getAccountType().equals("Savings") && fromAcc.getOwner() != toAcc.getOwner()) {
            throw new IllegalArgumentException("Cannot make transfer from Saving account to accounts you are not owner of!");
        }
        fromAcc.withdraw(amount);
        toAcc.deposit(amount);
        this.transferCassandraRepository.createTransfer(this.transferCassandraRepository.findLastId() + 1, transferDate, toAccount, fromAccount, amount);
        this.accountCassandraRepository.update(toAcc);
        this.accountCassandraRepository.update(fromAcc);
    }

    public void delete(int id) {
        this.transferCassandraRepository.deleteTransfer(id);
    }

    public void update(Transfer transfer) {
        this.transferCassandraRepository.update(transfer);
    }

    public Transfer getTransfer(int id) {
        return this.transferCassandraRepository.getTransfer(id);
    }

    public void update(Integer tranferId, Integer fromAccount, Integer toAccount, Double amount, LocalDate transferDate) throws Exception {
        if (!this.accountCassandraRepository.doesItContain(fromAccount) || !this.accountCassandraRepository.doesItContain(toAccount)) {
            throw new IllegalArgumentException("There are not such accounts!");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0!");
        }
        if (transferDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot set transfer to past date!");
        }
        Account toAcc = this.accountCassandraRepository.getAccount(toAccount);
        Account fromAcc = this.accountCassandraRepository.getAccount(fromAccount);
        if (!toAcc.isActive() || !fromAcc.isActive()) {
            throw new IllegalArgumentException("Inactive accounts!");
        }
        if (fromAcc.getAccountType().equals("Savings") && fromAcc.getOwner() != toAcc.getOwner()) {
            throw new IllegalArgumentException("Cannot make transfer from Saving account to accounts you are not owner of!");
        }
        Transfer transfer = this.transferCassandraRepository.getTransfer(tranferId);

        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(amount);
        transfer.setTransferDate(transferDate);
        this.transferCassandraRepository.update(transfer);
    }

    public List<Transfer> findByAccountSender(Integer accoutnID) {
        return this.transferCassandraRepository.findByAccountSender(accoutnID);
    }

    public List<Transfer> findByAccountReceiver(Integer accoutnID) {
        return this.transferCassandraRepository.findByAccountReceiver(accoutnID);
    }

    public List<Transfer> getAll() {
        return this.transferCassandraRepository.getAll();
    }
}
