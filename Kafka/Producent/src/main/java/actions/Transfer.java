package actions;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
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

    @BsonProperty("_transferId") @JsonbProperty("_transferId")
    private int transferId;

    @BsonProperty("toAccount") @JsonbProperty("toAccount")
    private int toAccount;

    @BsonProperty("fromAccount") @JsonbProperty("fromAccount")
    private int fromAccount;

    @BsonProperty("amount") @JsonbProperty("amount")
    private double amount;

    @BsonProperty("transferDate") @JsonbProperty("transferDate")
    private LocalDateTime transferDate;

    @BsonCreator @JsonbCreator
    public Transfer(
            @BsonProperty("_transferId") @JsonbProperty("_transferId") int transferID,
            @BsonProperty("fromAccount") @JsonbProperty("fromAccount") int fromAccount,
            @BsonProperty("toAccount") @JsonbProperty("toAccount") int toAccount,
            @BsonProperty("amount") @JsonbProperty("amount") double amount,
            @BsonProperty("transferDate") @JsonbProperty("transferDate") LocalDateTime transferDate
            ) {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.transferDate = transferDate;
        this.transferId = transferID;
    }

    public Transfer(int fromAccount, int toAccount, double amount, LocalDateTime transferDate) throws Exception  {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.amount = amount;
        this.transferDate = transferDate;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) throws Exception {
        if (transferDate.isBefore(LocalDateTime.now()))
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
                .append("transferID", transferId)
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

        return new EqualsBuilder()
                .append(transferId, transfer.transferId)
                .append(amount, transfer.amount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(transferId).toHashCode();
    }
}
