package model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.io.Serializable;


public class Account implements Serializable {
    @BsonProperty("_number")
    private int number;

    @BsonProperty("balance")
    private double balance;

    @BsonProperty("percentage")
    private double percentage;

    @BsonProperty("accountType")
    private AccountType accountType;

    @BsonProperty("client")
    private ClientAddress owner;

    @BsonProperty("active")
    private boolean active = true;

    @BsonCreator
    public Account(
            @BsonProperty("_number") int number,
            @BsonProperty("percentage") double percentage,
            @BsonProperty("accountType") AccountType accountType,
            @BsonProperty("client") ClientAddress owner,
            @BsonProperty("balance") double balance,
            @BsonProperty("active") boolean active) {
        this.number = number;
        this.percentage = percentage;
        this.accountType = accountType;
        this.owner = owner;
        this.balance = balance;
        this.active = active;
    }

    public Account(int number, double percentage, AccountType accountType, ClientAddress owner) throws Exception {
        this.number = number;
        this.percentage = percentage;
        this.accountType = accountType;
        this.owner = owner;

        if(percentage < 0) {
            throw new Exception("Oprocentowanie nie może być ujemne!");
        }
        if(owner == null) {
            throw new Exception("Nieokreslony wlasciciel!");
        }
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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public ClientAddress getOwner() {
        return owner;
    }

    public void setOwner(ClientAddress owner) {
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


