package actions;

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


public class Loan implements Serializable {

    @BsonProperty("_loanId") @JsonbProperty("_loanId")
    private int loanId;

    @BsonProperty("initAmount") @JsonbProperty("initAmount")
    private double initAmount;

    @BsonProperty("percentage") @JsonbProperty("percentage")
    private double percentage;

    @BsonProperty("beginDate") @JsonbProperty("beginDate")
    private LocalDateTime beginDate;

    @BsonProperty("endDate") @JsonbProperty("endDate")
    private LocalDateTime endDate;

    @BsonProperty("paidAmount") @JsonbProperty("paidAmount")
    private double paidAmount = 0;

    @BsonProperty("account") @JsonbProperty("account")
    private Account account;

    @BsonProperty("archive") @JsonbProperty("archive")
    private boolean archive = false;

    @BsonCreator
    public Loan(@BsonProperty("_loanId") @JsonbProperty("_loanId") int loandId,
                @BsonProperty("account") @JsonbProperty("account") Account account,
                @BsonProperty("initAmount") @JsonbProperty("initAmount") double initAmount,
                @BsonProperty("percentage") @JsonbProperty("percentage") double percentage,
                @BsonProperty("endDate") @JsonbProperty("endDate") LocalDateTime endDate,
                @BsonProperty("beginDate") @JsonbProperty("beginDate") LocalDateTime beginDate,
                @BsonProperty("paidAmount") @JsonbProperty("paidAmount") double paidAmount,
                @BsonProperty("archive") @JsonbProperty("archive") boolean archive) throws Exception {
        this(initAmount, percentage, endDate, account);
        this.loanId = loandId;
        this.beginDate = beginDate;
        this.paidAmount = paidAmount;
        this.archive = archive;
    }

    public Loan(double initAmount, double percentage, LocalDateTime endDate, Account account) throws Exception {
        this.beginDate = LocalDateTime.now();
        this.initAmount = initAmount;
        this.percentage = percentage;
        this.endDate = endDate;
        this.account = account;

        if(account.getAccountType() == AccountType.Savings) {
            throw new Exception("Nie mozna wziac kredytu na konto oszczednosciowe!");
        }
        account.deposit(initAmount);
    }

    public Loan() {

    }


    private double toRepay() {
        if(isArchive()) return 0;
        return getInitAmount()+(getInitAmount()*0.01*getPercentage())-getPaidAmount();
    }

    public void repay(double amount, LocalDateTime date) throws Exception {
        if(this.isArchive()) {
            throw new Exception("Nie można spłacić pożyczki już spłaconej");
        }
        this.account.withdraw(amount);

        if(isOverdue(date)) {
            paidAmount = (paidAmount + amount - overdueAmount());
            setEndDate(date.plusHours(720));
        } else paidAmount = paidAmount + amount;

        if(toRepay() <= 0) {
            account.deposit((-1)*toRepay());
            setArchive(true);
        }
    }

    public boolean isOverdue(LocalDateTime date) {
        return date.getSecond() > getEndDate().getSecond();
    }

    private double overdueAmount() {
        return 0.25*toRepay();
    }

    // SETTER AND GETTER
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

   public LocalDateTime getEndDate() {
        return endDate;
   }

    public void setEndDate(LocalDateTime date) throws Exception {
        endDate = date;
    }

   public double getInitAmount() {
        return this.initAmount;
   }

   public double getPaidAmount() {
        return this.paidAmount;
   }

   public double getPercentage() {
        return percentage;
   }


    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public void setInitAmount(double initAmount) {
        this.initAmount = initAmount;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    // UTIL FUNCTIONS
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("initAmount", initAmount)
                .append("percentage", percentage)
                .append("beginDate", beginDate)
                .append("endDate", endDate)
                .append("paidAmount", paidAmount)
                .append("account", account)
                .append("archive", archive)
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;

        return new EqualsBuilder().append(loanId, loan.loanId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(loanId).toHashCode();
    }
}
