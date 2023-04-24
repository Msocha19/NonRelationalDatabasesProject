package model.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import model.actions.Transfer;
import model.providers.TransferQueryProvider;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface TransferDao {

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    Transfer findById(int id);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void create(Transfer transfer);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void delete(Transfer transfer);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Update
    void update(Transfer transfer);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    List<Transfer> findByAccountSender(Integer accountID);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    List<Transfer> findByAccountReceiver(Integer accountID);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    List<Transfer> findByTransferDate(LocalDate transferDate);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    List<Transfer> getAll();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    Integer findLastId();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = TransferQueryProvider.class, entityHelpers = {Transfer.class})
    Boolean doesContain(int id);
}
