package model.actions;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.io.Serializable;
import java.time.LocalDate;

@Entity(defaultKeyspace = "bank")
@CqlName("transfers")
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Transfer implements Serializable {

    @PartitionKey
    @CqlName("transfer_id")
    private int transferId;

    @CqlName("to_account")
    private int toAccount;

    @CqlName("from_account")
    private int fromAccount;

    @CqlName("amount")
    private double amount;

    @ClusteringColumn
    @CqlName("transfer_date")
    private LocalDate transferDate;

    public Transfer(
            int transferID,
            LocalDate transferDate,
            int toAccount,
            int fromAccount,
            double amount
            ) {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.transferDate = transferDate;
        this.transferId = transferID;
    }

    public Transfer(int fromAccount, int toAccount, double amount, LocalDate transferDate) throws Exception  {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.transferDate = transferDate;

//        if(!fromAccount.isActive() || !toAccount.isActive()) throw new Exception("Konto jest nieaktywne");
//        if((fromAccount.getAccountType().equals("Savings")) && (fromAccount.getOwner()!= toAccount.getOwner()))
//            throw new Exception("Z konta oszczednosciowego nie mozna robic przelewow do nie swoich kont!");
//        if(amount<0)
//            throw new Exception("Wartosc przelewu musi byc dodatnia!");
//        this.fromAccount.withdraw(this.amount);
//        this.toAccount.deposit(this.amount);
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) throws Exception {
        if ( transferDate.isBefore(LocalDate.now()))
            throw new Exception("Data nie powinna być w przeszłości ");
        this.transferDate = transferDate;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setToAccount(int toAccount) {
        this.toAccount = toAccount;
    }

    public void setFromAccount(int fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getToAccount() {
        return toAccount;
    }

    public int getFromAccount() {
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
