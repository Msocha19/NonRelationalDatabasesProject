package managers;

import actions.Transfer;
import kafka.ProducerKafka;
import model.Account;
import model.AccountType;
import repository.CacheableTransferRepository;
import repository.Repository;
import repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransferManager {

    private final Repository<Transfer> repository;

    private final ProducerKafka kafkaConfig;

    private final AccountManager accountManager;

    public TransferManager() {
        this.repository = new CacheableTransferRepository();
        this.kafkaConfig = new ProducerKafka();
        this.accountManager = new AccountManager();
        try {
            this.kafkaConfig.createTopic();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.kafkaConfig.createProducent();
    }

    public void createTranfer(int fromAccount, int toAccount, double amount, LocalDateTime transferDate) throws Exception {
        Transfer transfer = new Transfer(fromAccount, toAccount, amount, transferDate);
        System.out.println(this.findLastId());
        transfer.setTransferId(this.findLastId() + 1);
        if (this.contains(transfer)) {
            throw new Exception("Transfer with this id already exists!");
        }

        Account from = this.accountManager.find(fromAccount);
        Account to = this.accountManager.find(toAccount);

        if(!from.isActive() || !to.isActive()) throw new Exception("Konto jest nieaktywne");
        if((from.getAccountType() == AccountType.Savings) && (from.getOwner()!= to.getOwner()))
            throw new Exception("Z konta oszczednosciowego nie mozna robic przelewow do nie swoich kont!");
        if(amount<0)
            throw new Exception("Wartosc przelewu musi byc dodatnia!");

        this.repository.add(transfer);
        AccountManager accountManager  = new AccountManager();
        accountManager.withdraw(amount, fromAccount);
        accountManager.deposit(amount, toAccount);
        this.kafkaConfig.sendTransfer(transfer);
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
        Transfer transfer = this.find(id);
        return this.accountManager.find(transfer.getToAccount());
    }

    public Account findFromAccount(Integer id) {
        Transfer transfer = this.find(id);
        return this.accountManager.find(transfer.getFromAccount());
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
            List<Integer> list = new ArrayList<>();
            for (Transfer transfer : t) {
                list.add(transfer.getTransferId());
            }
            return Collections.max(list);
        }
    }

    private boolean contains(Transfer transfer) {
        return this.findAll().contains(transfer);
    }


}
