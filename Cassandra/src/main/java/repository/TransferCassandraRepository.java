package repository;

import com.datastax.oss.driver.api.core.CqlSession;
import model.actions.Transfer;
import model.constants.TransferIds;
import model.dao.TransferDao;
import model.mappers.TransferMapper;
import model.mappers.TransferMapperBuilder;

import java.time.LocalDate;
import java.util.List;

public class TransferCassandraRepository {

    private TransferDao transferDao;

    public TransferCassandraRepository(CqlSession session) {
        TransferMapper transferMapper = new TransferMapperBuilder(session).build();
        this.transferDao = transferMapper.transferDao();
    }

    public void createTransfer(int id, LocalDate transferDate, int toAccount, int fromAccount, double amount) {
        this.transferDao.create(new Transfer(id, transferDate, toAccount, fromAccount, amount));
    }

    public Transfer getTransfer(int id) {
        return this.transferDao.findById(id);
    }

    public Transfer deleteTransfer(int id) {
        Transfer transfer = this.transferDao.findById(id);
        this.transferDao.delete(transfer);
        return transfer;
    }

    public void update(Transfer transfer) {
        this.transferDao.update(transfer);
    }

    public List<Transfer> findByAccountSender(Integer accountID) {
        return this.transferDao.findByAccountSender(accountID);
    }

    public List<Transfer> findByAccountReceiver(Integer accountID) {
        return this.transferDao.findByAccountReceiver(accountID);
    }

    public List<Transfer> findByTransferDate(LocalDate transferDate) {
        return this.transferDao.findByTransferDate(transferDate);
    }

    public List<Transfer> getAll() {
        return this.transferDao.getAll();
    }

    public Integer findLastId() {
        return this.transferDao.findLastId();
    }

    public Boolean doesContain(int id) {
        return this.transferDao.doesContain(id);
    }
}
