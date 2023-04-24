package model.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import model.Account;
import model.constants.AccountIds;

import java.util.ArrayList;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class AccountQueryProvider {

    private CqlSession session;

    private EntityHelper<Account> accountEntityHelper;

    public AccountQueryProvider(MapperContext ctx, EntityHelper<Account> accountEntityHelper) {
        this.session = ctx.getSession();
        this.accountEntityHelper = accountEntityHelper;
    }

    public List<Account> convertToList(List<Row> rows) {
        List<Account> accounts = new ArrayList<>();
        for (Row row : rows) {
            accounts.add(this.getAccount(row));
        }
        return accounts;
    }

//    public void create(Account account) {
//        session.execute(
//                session.prepare(accountEntityHelper.insert().build())
//                        .bind()
//                        .setInt(AccountIds.NUMBER, account.getNumber())
//                        .setDouble(AccountIds.BALANCE, account.getBalance())
//                        .setDouble(AccountIds.PERCENTAGE, account.getPercentage())
//                        .setString(AccountIds.TYPE, account.getAccountType())
//                        .setInt(AccountIds.OWNER, account.getOwner())
//                        .setBoolean(AccountIds.ACTIVE, account.isActive()));
//    }

    public Account findByNumber(int number) {
        Select selectAccount = QueryBuilder
                .selectFrom(AccountIds.ACCOUNTS)
                .all()
                .where(Relation.column(AccountIds.NUMBER).isEqualTo(literal(number)));
        Row row = session.execute(selectAccount.build()).one();
        return getAccount(row);
    }

//    public void delete(Account account) {
//        session.execute(
//                session.prepare(accountEntityHelper.deleteByPrimaryKey().build())
//                        .bind()
//        );
//    }

    public List<Account> findAllAccountsForClient(int clientID) {
        Select selectAccounts = QueryBuilder
                .selectFrom(AccountIds.ACCOUNTS)
                .all()
                .allowFiltering()
                .where(Relation.column(AccountIds.OWNER).isEqualTo(literal(clientID)));
        return this.convertToList(session.execute(selectAccounts.build()).all());
    }

    public List<Account> getAll() {
        Select getAll = QueryBuilder
                .selectFrom(AccountIds.ACCOUNTS)
                .all();
        return this.convertToList(session.execute(getAll.build()).all());
    }

    public List<Account> findByType(String type) {
        Select getByType = QueryBuilder
                .selectFrom(AccountIds.ACCOUNTS)
                .all()
                .allowFiltering()
                .where(Relation.column(AccountIds.TYPE).isEqualTo(literal(type)));
        return this.convertToList(session.execute(getByType.build()).all());
    }

    public Boolean doesContain(int number) {
        Select doesItContain = QueryBuilder
                .selectFrom(AccountIds.ACCOUNTS)
                .all()
                .where(Relation.column(AccountIds.NUMBER).isEqualTo(literal(number)));
        Row row = session.execute(doesItContain.build()).one();
        return row != null;
    }

    private Account getAccount(Row account) {
        return new Account(
                account.getInt(AccountIds.NUMBER),
                account.getDouble(AccountIds.BALANCE),
                account.getDouble(AccountIds.PERCENTAGE),
                account.getString(AccountIds.TYPE),
                account.getInt(AccountIds.OWNER),
                account.getBoolean(AccountIds.ACTIVE)
        );
    }
}
