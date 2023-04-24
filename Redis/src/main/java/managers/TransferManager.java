package managers;

import actions.Transfer;
import model.Account;
import repository.CacheableTransferRepository;
import repository.Repository;
import repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TransferManager {

    private final Repository<Transfer> repository;

    public TransferManager() {
        this.repository = new CacheableTransferRepository();
    }

    public void createTranfer(Account fromAccount, Account toAccount, double amount, LocalDateTime transferDate) throws Exception {
        Transfer transfer = new Transfer(fromAccount, toAccount, amount, transferDate);
        transfer.setTransferId(this.findLastId() + 1);
        if (this.contains(transfer)) {
            throw new Exception("Transfer with this id already exists!");
        }

        this.repository.add(transfer);
        AccountManager accountManager  = new AccountManager();
        accountManager.withdraw(amount, fromAccount.getNumber());
        accountManager.deposit(amount, toAccount.getNumber());
    }

    public void deleteTranferRecord(Integer id) throws Exception {
        Transfer transfer = repository.find("_transferId", id);
        if (!this.contains(transfer)) {
            throw new Exception("This transfer does not exist!");
        }

        repository.delete("_transferId", id);
    }

    public List<Transfer> findAll() {
        return this.repository.getAll();
    }

    public Account findToAccount(Integer id) {
        return (Account) this.repository.findAllNested(this.find(id)).get(0);
    }

    public Account findFromAccount(Integer id) {
        return (Account) this.repository.findAllNested(this.find(id)).get(1);
    }

    public Transfer find(Integer id) {
        return this.repository.find("_transferId", id);
    }

    public void deleteAll() {
        this.repository.wipeAll();
    }

    private int findLastId() {
        List<Transfer> t = this.findAll();
        if (t.isEmpty()) {
            return 0;
        } else {
            return t.get(t.size() - 1).getTransferId();
        }
    }

    private boolean contains(Transfer transfer) {
        return this.findAll().contains(transfer);
    }


}
