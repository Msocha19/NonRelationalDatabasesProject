package model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
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

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getToAccount() {
        return toAccount;
    }

    public void setToAccount(int toAccount) {
        this.toAccount = toAccount;
    }

    public int getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(int fromAccount) {
        this.fromAccount = fromAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }
}
