package model;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.io.Serializable;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

public abstract class Client implements Serializable {

    @PartitionKey
    @CqlName("id")
    private int id;

    @CqlName("income")
    private double income;

    @CqlName("phone_number")
    private String phoneNumber;

    @CqlName("accounts")
    protected List<Integer> accounts;

    @CqlName("country")
    private String country;

    @CqlName("city")
    private String city;

    @CqlName("street")
    private String street;

    @CqlName("number")
    private String number;

    @ClusteringColumn
    @CqlName("discriminator")
    private String discriminator;


    public Client(List<Integer> accounts,
                  String city,
                  String country,
                  int id,
                  double income,
                  String number,
                  String phoneNumber,
                  String street,
                  String discriminator) {
        this.id = id;
        this.income = income;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.street = street;
        this.number = number;
        this.accounts = accounts;
        this.discriminator = discriminator;
    }

    public Client(int id,
                  double income,
                  String phoneNumber,
                  String country,
                  String city,
                  String street,
                  String number,
                  String discriminator) {
        this.id = id;
        this.income = income;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.street = street;
        this.number = number;
        this.accounts = new ArrayList<>();
        this.discriminator = discriminator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        id = id;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Integer> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Integer> accounts) {
        this.accounts = accounts;
    }

    public void openAccount(Account account) {
        if (!this.accounts.contains(account.getNumber())) {
            this.accounts.add(account.getNumber());
        }
    }

    public void closeAccount(Account account) {
        if (this.accounts.contains(account.getNumber())) {
            this.accounts.remove(this.accounts.indexOf(account.getNumber()));
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

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
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

        Client that = (Client) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}

