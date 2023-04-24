package model.constants;

import com.datastax.oss.driver.api.core.CqlIdentifier;

public class AccountIds {
    public static final CqlIdentifier NUMBER = CqlIdentifier.fromCql("number");
    public static final CqlIdentifier BALANCE = CqlIdentifier.fromCql("balance");
    public static final CqlIdentifier PERCENTAGE = CqlIdentifier.fromCql("percentage");
    public static final CqlIdentifier TYPE = CqlIdentifier.fromCql("type");
    public static final CqlIdentifier OWNER = CqlIdentifier.fromCql("owner");
    public static final CqlIdentifier ACTIVE = CqlIdentifier.fromCql("active");

    public static final CqlIdentifier ACCOUNTS = CqlIdentifier.fromCql("accounts");
}
