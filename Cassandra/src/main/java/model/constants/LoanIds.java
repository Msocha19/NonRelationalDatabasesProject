package model.constants;

import com.datastax.oss.driver.api.core.CqlIdentifier;

public class LoanIds {
    public static final CqlIdentifier LOAN_ID = CqlIdentifier.fromCql("id");
    public static final CqlIdentifier INIT_AMOUNT = CqlIdentifier.fromCql("init_amount");
    public static final CqlIdentifier PERCENTAGE = CqlIdentifier.fromCql("percentage");
    public static final CqlIdentifier BEGIN_DATE= CqlIdentifier.fromCql("begin_date");
    public static final CqlIdentifier END_DATE = CqlIdentifier.fromCql("end_date");
    public static final CqlIdentifier PAID_AMOUNT = CqlIdentifier.fromCql("paid_amount");
    public static final CqlIdentifier ACCOUNT = CqlIdentifier.fromCql("account");
    public static final CqlIdentifier ARCHIVE = CqlIdentifier.fromCql("archive");
    public static final CqlIdentifier LOANS = CqlIdentifier.fromCql("loans");
}
