package model.constants;

import com.datastax.oss.driver.api.core.CqlIdentifier;

public class ClientIds {
    public static final CqlIdentifier ID = CqlIdentifier.fromCql("id");
    public static final CqlIdentifier COUNTRY = CqlIdentifier.fromCql("country");
    public static final CqlIdentifier INCOME = CqlIdentifier.fromCql("income");
    public static final CqlIdentifier PHONE_NUMBER = CqlIdentifier.fromCql("phone_number");
    public static final CqlIdentifier ACCOUNTS = CqlIdentifier.fromCql("accounts");
    public static final CqlIdentifier CITY = CqlIdentifier.fromCql("city");
    public static final CqlIdentifier STREET = CqlIdentifier.fromCql("street");
    public static final CqlIdentifier NUMBER = CqlIdentifier.fromCql("number");
    public static final CqlIdentifier NAME = CqlIdentifier.fromCql("name");
    public static final CqlIdentifier NIP = CqlIdentifier.fromCql("NIP");
    public static final CqlIdentifier SURNAME = CqlIdentifier.fromCql("surname");
    public static final CqlIdentifier PERSONAL_NUMBER= CqlIdentifier.fromCql("personal_number");
    public static final CqlIdentifier DISCRIMINATOR = CqlIdentifier.fromCql("discriminator");

    public static final CqlIdentifier CLIENTS = CqlIdentifier.fromCql("clients");
}
