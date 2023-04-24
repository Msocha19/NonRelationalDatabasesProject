package model.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import model.actions.Transfer;
import model.constants.TransferIds;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class TransferQueryProvider {

    private CqlSession session;

    private EntityHelper<Transfer> transferEntityHelper;

    public TransferQueryProvider(MapperContext ctx, EntityHelper<Transfer> transferEntityHelper) {
        this.session = ctx.getSession();
        this.transferEntityHelper = transferEntityHelper;
    }

    public List<Transfer> convertToList(List<Row> rows) {
        List<Transfer> transfers = new ArrayList<>();
        for (Row row : rows) {
            transfers.add(this.getTransfer(row));
        }
        return transfers;
    }

//    public void create(Transfer transfer) {
//        session.execute(
//                session.prepare(transferEntityHelper.insert().build())
//                        .bind()
//                        .setInt(TransferIds.TRANSFER_ID, transfer.getTransferId())
//                        .setInt(TransferIds.FROM_ACCOUNT, transfer.getFromAccount())
//                        .setInt(TransferIds.TO_ACCOUNT, transfer.getToAccount())
//                        .setDouble(TransferIds.AMOUNT, transfer.getAmount())
//                        .setLocalDate(TransferIds.TRANSFER_DATE, transfer.getTransferDate())
//        );
//    }

    public Transfer findById(int id) {
        Select selectTransfer = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all()
                .where(Relation.column(TransferIds.TRANSFER_ID).isEqualTo(literal(id)));
        Row row = session.execute(selectTransfer.build()).one();
        return getTransfer(row);
    }

    public List<Transfer> getAll() {
        Select selectTransfer = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all();
        return this.convertToList(session.execute(selectTransfer.build()).all());
    }

    public List<Transfer> findByTransferDate(LocalDate transferDate) {
        Select findBy = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all()
                .where(Relation.column(TransferIds.TRANSFER_DATE).isGreaterThanOrEqualTo(literal(transferDate)));
        return this.convertToList(session.execute(findBy.build()).all());
    }

    public List<Transfer> findByAccountReceiver(Integer accountID) {
        Select findBy = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all()
                .allowFiltering()
                .where(Relation.column(TransferIds.TO_ACCOUNT).isEqualTo(literal(accountID)));
        return this.convertToList(session.execute(findBy.build()).all());
    }

    public List<Transfer> findByAccountSender(Integer accountID) {
        Select findBy = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all()
                .allowFiltering()
                .where(Relation.column(TransferIds.FROM_ACCOUNT).isEqualTo(literal(accountID)));
        return this.convertToList(session.execute(findBy.build()).all());
    }

    public Integer findLastId() {
        Select lastID = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all();
        List<Row> rows = session.execute(lastID.build()).all();
        List<Integer> all = new ArrayList<>();
        if (rows.size() > 0) {
            for (Row row : rows) {
                all.add(row.getInt(TransferIds.TRANSFER_ID));
            }
            return Collections.max(all);
        }
        return 0;
    }

    public Boolean doesContain(int id) {
        Select doesContain = QueryBuilder
                .selectFrom(TransferIds.TRANSFERS)
                .all()
                .where(Relation.column(TransferIds.TRANSFER_ID).isEqualTo(literal(id)));
        Row row = session.execute(doesContain.build()).one();
        return row != null;
    }


    //    private void delete(Transfer transfer) {
//        session.execute(
//          session.prepare(transferEntityHelper.deleteByPrimaryKey().build())
//                  .bind()
//        );
//    }

    private Transfer getTransfer(Row transfer) {
        return new Transfer(
          transfer.getInt(TransferIds.TRANSFER_ID),
          transfer.getLocalDate(TransferIds.TRANSFER_DATE),
          transfer.getInt(TransferIds.TO_ACCOUNT),
          transfer.getInt(TransferIds.FROM_ACCOUNT),
          transfer.getDouble(TransferIds.AMOUNT)
        );
    }
}
