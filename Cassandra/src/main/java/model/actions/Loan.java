package model.actions;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import model.Account;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.io.Serializable;
import java.time.LocalDate;

@Entity(defaultKeyspace = "bank")
@CqlName("loans")
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Loan implements Serializable {

    @PartitionKey
    @CqlName("id")
    private int loanId;

    @CqlName("init_amount")
    private double initAmount;

    @CqlName("percentage")
    private double percentage;

    @ClusteringColumn(0)
    @CqlName("begin_date")
    private LocalDate beginDate;

    @ClusteringColumn(1)
    @CqlName("end_date")
    private LocalDate endDate;

    @CqlName("paid_amount")
    private double paidAmount = 0;

    @CqlName("account")
    private int account;

    @CqlName("archive")
    private boolean archive = false;

    public Loan(int loandId,
                LocalDate beginDate,
                LocalDate endDate,
                int account,
                double initAmount,
                double paidAmount,
                double percentage,
                boolean archive) {
        this.loanId = loandId;
        this.account = account;
        this.initAmount = initAmount;
        this.percentage = percentage;
        this.endDate = endDate;
        this.beginDate = beginDate;
        this.paidAmount = paidAmount;
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

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Loan() {

    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

   public LocalDate getEndDate() {
        return endDate;
   }

    public void setEndDate(LocalDate date) throws Exception {
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

    public void repay(double amount, LocalDate date, Account account) throws Exception {
        if( !this.isArchive()) {
            account.withdraw(amount);

            if(isOverdue(date)) {
                paidAmount = (paidAmount + amount - overdueAmount());
                setEndDate(date.plusDays(30));
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

    public boolean isOverdue(LocalDate date) {
        if (date.isAfter(this.getEndDate())) {
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
