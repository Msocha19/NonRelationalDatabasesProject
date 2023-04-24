package model;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.io.Serializable;

@Entity(defaultKeyspace = "bank")
@CqlName("accounts")
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Account implements Serializable {

    @PartitionKey
    @CqlName("number")
    private int number;

    @CqlName("balance")
    private double balance;

    @CqlName("percentage")
    private double percentage;

    @ClusteringColumn
    @CqlName("type")
    private String accountType;

    @CqlName("owner")
    private int owner;

    @CqlName("active")
    private boolean active = true;

    public Account(
            int number,
            String accountType,
            double balance,
            double percentage,
            int owner,
            boolean active) {
        this.number = number;
        this.percentage = percentage;
        this.accountType = accountType;
        this.owner = owner;
        this.balance = balance;
        this.active = active;
    }

    public Account(int number, double percentage, String accountType, int owner) {
        this.number = number;
        this.percentage = percentage;
        this.accountType = accountType;
        this.owner = owner;
    }

    public Account(int number, double balance, double percentage, String accountType, int owner, boolean active) {
        this.number = number;
        this.balance = balance;
        this.percentage = percentage;
        this.accountType = accountType;
        this.owner = owner;
        this.active = active;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("number", number)
                .append("balance", balance)
                .append("percentage", percentage)
                .append("accountType", accountType)
                .append("owner", owner)
                .append("active", active)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return new EqualsBuilder().append(number, account.number).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(number).toHashCode();
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) throws Exception {
        if (getBalance() >= amount) {
            this.balance = (this.getBalance() - amount);
        } else {
            throw new Exception("Brak pieniędzy na koncie do wypłaty ");
        }
    }
}


