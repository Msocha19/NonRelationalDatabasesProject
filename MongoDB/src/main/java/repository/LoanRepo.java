package repository;

import actions.Loan;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import model.Account;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class LoanRepo extends MongoRepository<Loan> {

    public LoanRepo() {
        super("loan", Loan.class);
    }

    @Override
    public Account findNested(Object obj) {
        Loan loan = (Loan) obj;
        MongoCollection<Loan> objCollection = super.bankDB.getCollection(super.collectionName, Loan.class);
        Bson filter = Filters.eq("_loanId", loan.getLoanId());
        ArrayList<Account> list = objCollection.aggregate(List.of(Aggregates.match(filter),
                Aggregates.replaceRoot("$account")), Account.class).into(new ArrayList<>());
        return list.get(0);
    }
}
