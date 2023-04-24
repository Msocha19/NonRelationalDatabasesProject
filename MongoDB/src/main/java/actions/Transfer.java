package actions;

import model.Account;
import model.AccountType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Transfer implements Serializable {

    @BsonProperty("_transferId")
    private int transferId;

    @BsonProperty("toAccount")
    private Account toAccount;

    @BsonProperty("fromAccount")
    private Account fromAccount;

    @BsonProperty("amount")
    private double amount;

    @BsonProperty("transferDate")
    private LocalDateTime transferDate;

    @BsonCreator
    public Transfer(
            @BsonProperty("_transferId") int transferID,
            @BsonProperty("fromAccount") Account fromAccount,
            @BsonProperty("toAccount") Account toAccount,
            @BsonProperty("amount") double amount,
            @BsonProperty("transferDate") LocalDateTime transferDate
            ) {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.transferDate = transferDate;
        this.transferId = transferID;
    }

    public Transfer(Account fromAccount, Account toAccount, double amount, LocalDateTime transferDate) throws Exception  {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.transferDate = transferDate;

        if(!fromAccount.isActive() || !toAccount.isActive()) throw new Exception("Konto jest nieaktywne");
        if((fromAccount.getAccountType() == AccountType.Savings) && (fromAccount.getOwner()!= toAccount.getOwner()))
            throw new Exception("Z konta oszczednosciowego nie mozna robic przelewow do nie swoich kont!");
        if(amount<0)
            throw new Exception("Wartosc przelewu musi byc dodatnia!");
        this.fromAccount.withdraw(this.amount);
        this.toAccount.deposit(this.amount);
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) throws Exception {
        if ( transferDate.isBefore(LocalDateTime.now()))
            throw new Exception("Data nie powinna być w przeszłości ");
        this.transferDate = transferDate;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("toAccount", toAccount)
                .append("fromAccount", fromAccount)
                .append("amount", amount)
                .append("transferDate", transferDate)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        return new EqualsBuilder().append(transferId, transfer.transferId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(transferId).toHashCode();
    }
}
