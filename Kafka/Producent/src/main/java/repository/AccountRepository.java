package repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import model.Account;
import model.ClientAddress;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository extends MongoRepository<Account> {

    public AccountRepository() {
        super("account", Account.class);
    }
    @Override
    public ClientAddress findNested(Object obj) {
        Account acc = (Account) obj;
        MongoCollection<Account> objCollection = super.bankDB.getCollection(super.collectionName, Account.class);
        Bson filter = Filters.eq("_number", acc.getNumber());
        ArrayList<ClientAddress> list = objCollection.aggregate(List.of(Aggregates.match(filter),
                Aggregates.replaceRoot("$client")), ClientAddress.class).into(new ArrayList<>());
        return list.get(0);
    }
}
