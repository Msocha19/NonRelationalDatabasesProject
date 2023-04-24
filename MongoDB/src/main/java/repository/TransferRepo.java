package repository;

import actions.Transfer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import model.Account;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class TransferRepo extends MongoRepository<Transfer> {

    public TransferRepo() {
        super("transfer", Transfer.class);
    }

    @Override
    public List<Account> findAllNested(Object obj) {
        Transfer tr = (Transfer) obj;
        MongoCollection<Transfer> objCollection = super.bankDB.getCollection(super.collectionName, Transfer.class);
        Bson filter = Filters.eq("_transferId", tr.getTransferId());
        ArrayList<Account> to = objCollection.aggregate(List.of(Aggregates.match(filter),
                Aggregates.replaceRoot("$toAccount")), Account.class).into(new ArrayList<>());
        ArrayList<Account> from = objCollection.aggregate(List.of(Aggregates.match(filter),
                Aggregates.replaceRoot("$fromAccount")), Account.class).into(new ArrayList<>());
        to.add(from.get(0));
        return to;
    }
}
