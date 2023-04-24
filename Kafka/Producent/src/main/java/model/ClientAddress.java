package model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@BsonDiscriminator(key="_clientAdress")
public class ClientAddress implements Serializable {
    @BsonProperty("_id") @JsonbProperty("_id")
    private int id;

    @BsonProperty("income") @JsonbProperty("income")
    private double income;

    @BsonProperty("phoneNumber") @JsonbProperty("phoneNumber")
    private String phoneNumber;

    @BsonProperty("accounts") @JsonbProperty("accounts")
    protected List<Integer> accounts;

    @BsonProperty("country") @JsonbProperty("country")
    private String country;

    @BsonProperty("city") @JsonbProperty("city")
    private String city;

    @BsonProperty("street") @JsonbProperty("street")
    private String street;

    @BsonProperty("number") @JsonbProperty("number")
    private String number;

    @BsonCreator @JsonbCreator
    public ClientAddress (@BsonProperty("_id") @JsonbProperty("_id") int id,
                          @BsonProperty("income") @JsonbProperty("income") double income,
                          @BsonProperty("phoneNumber") @JsonbProperty("phoneNumber") String phoneNumber,
                          @BsonProperty("country") @JsonbProperty("country") String country,
                          @BsonProperty("city") @JsonbProperty("city") String city,
                          @BsonProperty("street") @JsonbProperty("street") String street,
                          @BsonProperty("number") @JsonbProperty("number") String number,
                          @BsonProperty("accounts") @JsonbProperty("accounts") List<Integer> accounts) {
        this(id, income, phoneNumber, country, city, street, number);
        this.accounts = accounts;
    }

    public ClientAddress(int id, double income, String phoneNumber, String country, String city, String street, String number) {
        this.id = id;
        this.income = income;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.street = street;
        this.number = number;
        this.accounts = new ArrayList<>();
    }

    public int getId() {
        return id;
    }


    public double getIncome() {
        return income;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public List<Integer> getAccounts() {
        return accounts;
    }

    public void openAccount(Account account) {
        if (!this.accounts.contains(account.getNumber())) {
            this.accounts.add(account.getNumber());
        }
    }

    public void closeAccount(Account account) {
        if (this.accounts.contains(account.getNumber())) {
            this.accounts.remove((Integer) account.getNumber());
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Id", id)
                .append("income", income)
                .append("phoneNumber", phoneNumber)
                .append("accounts", accounts)
                .append("country", country)
                .append("city", city)
                .append("street", street)
                .append("number", number)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClientAddress that = (ClientAddress) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}

