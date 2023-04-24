package model.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import model.actions.Loan;
import model.constants.LoanIds;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class LoanQueryProvider {

    private CqlSession session;

    private EntityHelper<Loan> loanEntityHelper;

    public LoanQueryProvider(MapperContext ctx, EntityHelper<Loan> loanEntityHelper) {
        this.session = ctx.getSession();
        this.loanEntityHelper = loanEntityHelper;
    }

    public List<Loan> convertToList(List<Row> rows) {
        List<Loan> loans = new ArrayList<>();
        for (Row row : rows) {
            loans.add(this.getLoan(row));
        }
        return loans;
    }

//    public void create(Loan loan) {
//        session.execute(
//                session.prepare(loanEntityHelper.insert().build())
//                        .bind()
//                        .setInt(LoanIds.LOAN_ID, loan.getLoanId())
//                        .setInt(LoanIds.ACCOUNT, loan.getAccount())
//                        .setDouble(LoanIds.INIT_AMOUNT, loan.getInitAmount())
//                        .setDouble(LoanIds.PERCENTAGE, loan.getPercentage())
//                        .setLocalDate(LoanIds.END_DATE, loan.getEndDate())
//                        .setLocalDate(LoanIds.BEGIN_DATE, loan.getBeginDate())
//                        .setDouble(LoanIds.PAID_AMOUNT, loan.getPaidAmount())
//                        .setBoolean(LoanIds.ARCHIVE, loan.isArchive())
//        );
//    }

    public Loan findById(int id) {
        Select selectLoan = QueryBuilder
                .selectFrom(LoanIds.LOANS)
                .all()
                .where(Relation.column(LoanIds.LOAN_ID).isEqualTo(literal(id)));
        Row row = session.execute(selectLoan.build()).one();
        return getLoan(row);
    }

//    public void delete(Loan loan) {
//        session.execute(
//                session.prepare(loanEntityHelper.deleteByPrimaryKey().build())
//                        .bind()
//        );
//    }

    public List<Loan> getAll() {
        Select getAll = QueryBuilder
                .selectFrom(LoanIds.LOANS)
                .all();
        return this.convertToList(session.execute(getAll.build()).all());
    }

    public List<Loan> getAllArchivedOrNot(Boolean archived) {
        Select getActive = QueryBuilder
                .selectFrom(LoanIds.LOANS)
                .all()
                .allowFiltering()
                .where(Relation.column(LoanIds.ARCHIVE).isEqualTo(literal(archived)));
        return this.convertToList(session.execute(getActive.build()).all());
    }

//    public List<Loan> getLoansFromRange(LocalDate begin, LocalDate end) {
//        Select fromRange = QueryBuilder
//                .selectFrom(LoanIds.LOANS)
//                .all()
//                .where(Relation.column(LoanIds.BEGIN_DATE).isGreaterThanOrEqualTo(literal(begin)))
//                .where(Relation.column(LoanIds.END_DATE).isLessThanOrEqualTo(literal(end)));
//        return this.convertToList(session.execute(fromRange.build()).all());
//    }

    public Loan findLoanByAccount(int accountID) {
        Select findLoan = QueryBuilder
                .selectFrom(LoanIds.LOANS)
                .all()
                .allowFiltering()
                .where(Relation.column(LoanIds.ACCOUNT).isEqualTo(literal(accountID)));
        Row row = session.execute(findLoan.build()).one();
        return this.getLoan(row);
    }

    public Boolean doesContain(int id) {
        Select doesContain = QueryBuilder
                .selectFrom(LoanIds.LOANS)
                .all()
                .where(Relation.column(LoanIds.LOAN_ID).isEqualTo(literal(id)));
        Row row = session.execute(doesContain.build()).one();
        return row != null;
    }

    public Integer findLastId() {
        Select lastID = QueryBuilder
                .selectFrom(LoanIds.LOANS)
                .all();
        List<Row> rows = session.execute(lastID.build()).all();
        List<Integer> all = new ArrayList<>();
        if (rows.size() > 0) {
            for (Row row : rows) {
                all.add(row.getInt(LoanIds.LOAN_ID));
            }
            return Collections.max(all);
        }
        return 0;
    }
    private Loan getLoan(Row loan) {
        return new Loan(
                loan.getInt(LoanIds.LOAN_ID),
                loan.getLocalDate(LoanIds.BEGIN_DATE),
                loan.getLocalDate(LoanIds.END_DATE),
                loan.getInt(LoanIds.ACCOUNT),
                loan.getDouble(LoanIds.INIT_AMOUNT),
                loan.getDouble(LoanIds.PAID_AMOUNT),
                loan.getDouble(LoanIds.PERCENTAGE),
                loan.getBoolean(LoanIds.ARCHIVE)
        );
    }
}
