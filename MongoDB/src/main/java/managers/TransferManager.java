package managers;

import actions.Transfer;
import model.Account;
import repository.MongoRepository;
import repository.TransferRepo;

import java.time.LocalDateTime;
import java.util.List;

public class TransferManager {

    private MongoRepository<Transfer> repository;

    public TransferManager() {
        this.repository = new TransferRepo();
    }

    public void createTranfer(Account fromAccount, Account toAccount, double amount, LocalDateTime transferDate) {
        try {
            Transfer transfer = new Transfer(fromAccount, toAccount, amount, transferDate);
            transfer.setTransferId(this.findLastId() + 1);
            if (this.contains(transfer)) {
                throw new Exception("Transfer with this id already exists!");
            } else {
                this.repository.add(transfer);
            }
            AccountManager accountManager  = new AccountManager();
            accountManager.withdraw(amount, fromAccount.getNumber());
            accountManager.deposit(amount, toAccount.getNumber());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void update(Object id, Object field, Object newValue) throws Exception {
        Transfer transfer = repository.find("_transferId", id);
        if (this.contains(transfer)) {
            repository.update("_transferId", field, newValue, id);
        } else {
            throw new Exception("This transfer does not exist!");
        }
    }*/

    public void deleteTranferRecord(Integer id) throws Exception {
        Transfer transfer = repository.find("_transferId", id);
        if (this.contains(transfer)) {
            repository.delete("_transferId", id);
        } else {
            throw new Exception("This transfer does not exist!");
        }
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
        if (this.findAll().size() == 0) {
            return 0;
        } else {
            return this.findAll().get(this.findAll().size() - 1).getTransferId();
        }
    }

    private boolean contains(Transfer transfer) {
        if (this.findAll().contains(transfer)) {
            return true;
        }
        return false;
    }


}
