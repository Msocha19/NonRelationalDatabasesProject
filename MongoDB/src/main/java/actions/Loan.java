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


public class Loan implements Serializable {

    @BsonProperty("_loanId")
    private int loanId;

    @BsonProperty("initAmount")
    private double initAmount;

    @BsonProperty("percentage")
    private double percentage;

    @BsonProperty("beginDate")
    private LocalDateTime beginDate;

    @BsonProperty("endDate")
    private LocalDateTime endDate;

    @BsonProperty("paidAmount")
    private double paidAmount = 0;

    @BsonProperty("account")
    private Account account;

    @BsonProperty("archive")
    private boolean archive = false;

    @BsonCreator
    public Loan(@BsonProperty("_loanId") int loandId,
                @BsonProperty("account") Account account,
                @BsonProperty("initAmount") double initAmount,
                @BsonProperty("percentage") double percentage,
                @BsonProperty("endDate") LocalDateTime endDate,
                @BsonProperty("beginDate") LocalDateTime beginDate,
                @BsonProperty("paidAmount") double paidAmount,
                @BsonProperty("archive") boolean archive) throws Exception {
        this.loanId = loandId;
        this.account = account;
        this.initAmount = initAmount;
        this.percentage = percentage;
        this.endDate = endDate;
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

    public Loan() {

    }

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

    private double toRepay() {
        if(isArchive()) return 0;
        return getInitAmount()+(getInitAmount()*0.01*getPercentage())-getPaidAmount();
    }


    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public void repay(double amount, LocalDateTime date) throws Exception {
        if( !this.isArchive()) {
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
        else {
            throw new Exception("Nie można spłacić pożyczki już spłaconej");
        }
    }

    public boolean isOverdue(LocalDateTime date) {
        if (date.getSecond() > getEndDate().getSecond()) {
            return true;
        }
        return false;
    }

    private double overdueAmount() {
        return 0.25*toRepay();
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
