package model.constants;

import com.datastax.oss.driver.api.core.CqlIdentifier;

public class TransferIds {
    public static final CqlIdentifier TRANSFER_ID = CqlIdentifier.fromCql("transfer_id");
    public static final CqlIdentifier TO_ACCOUNT = CqlIdentifier.fromCql("to_account");
    public static final CqlIdentifier FROM_ACCOUNT = CqlIdentifier.fromCql("from_account");
    public static final CqlIdentifier AMOUNT = CqlIdentifier.fromCql("amount");
    public static final CqlIdentifier TRANSFER_DATE = CqlIdentifier.fromCql("transfer_date");
    public static final CqlIdentifier TRANSFERS = CqlIdentifier.fromCql("transfers");
}
