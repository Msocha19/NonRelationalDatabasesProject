import actions.Loan;
import managers.AccountManager;
import managers.ClientManager;
import managers.LoanManager;
import model.Account;
import model.AccountType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LoanManagerTest {
    private static AccountManager accountManager;
    private static ClientManager clientManager;
    private static LoanManager loanManager;

    public LoanManagerTest() {
        accountManager = new AccountManager();
        clientManager = new ClientManager();
        loanManager = new LoanManager();
    }

    public void innit() {
        try {
            clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
            clientManager.createPerson(2, 1240, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");
            clientManager.createCompany(3, 3560, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
            clientManager.createPerson(4, 2435345, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");
            accountManager.createAccount(1, 1.2, AccountType.Normal, clientManager.find(1));
            accountManager.createAccount(2, 1.3, AccountType.Normal, clientManager.find(2));
            accountManager.createAccount(3, 1.4, AccountType.Normal, clientManager.find(3));
            accountManager.createAccount(4, 1.5, AccountType.Normal, clientManager.find(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void end() {
        try {
            clientManager.deleteClient(1);
            clientManager.deleteClient(2);
            clientManager.deleteClient(3);
            clientManager.deleteClient(4);
            accountManager.deleteAccount(1);
            accountManager.deleteAccount(2);
            accountManager.deleteAccount(3);
            accountManager.deleteAccount(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createTest() {
        innit();
        try {
            loanManager.createLoan(2, 2, LocalDateTime.now().plusDays(12), accountManager.find(1));
            Assertions.assertEquals(loanManager.findAll().get(0).getLoanId(), 1);
            Assertions.assertEquals(loanManager.find(1).getInitAmount(), 2);
            Assertions.assertEquals(loanManager.find(1).getClass(), Loan.class);
            Assertions.assertThrows(Exception.class, () -> loanManager.createLoan(2, 2, LocalDateTime.now().minusDays(12), accountManager.find(2)));
            loanManager.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        end();
    }

    @Test
    public void deleteTest() {
        innit();
        try {
            loanManager.createLoan(2, 2, LocalDateTime.now().plusDays(12), accountManager.find(1));
            loanManager.createLoan(3, 3, LocalDateTime.now().plusDays(5), accountManager.find(2));
            loanManager.archiviseLoan(1);
            loanManager.archiviseLoan(2);
            Assertions.assertEquals(loanManager.find(1).isArchive(), true);
            Assertions.assertThrows(Exception.class, ()-> loanManager.archiviseLoan(5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loanManager.deleteAll();
        end();
    }

    @Test
    public void findTest() {
        innit();
        try {
            loanManager.createLoan(2, 2, LocalDateTime.now().plusDays(12), accountManager.find(1));
            loanManager.createLoan(3, 3, LocalDateTime.now().plusDays(5), accountManager.find(2));
            Assertions.assertEquals(loanManager.find(1).getPercentage(), 2);
            Assertions.assertEquals(loanManager.find(2).getPercentage(), 3);
            Assertions.assertEquals(loanManager.findAll().size(), 2);
            Assertions.assertEquals(loanManager.findAccount(1).getPercentage(), 1.2);
            Assertions.assertEquals(loanManager.findAccount(1).getClass(), Account.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loanManager.deleteAll();
        end();
    }

    @Test
    public void updateTest() {
        innit();
        try {
            loanManager.createLoan(2, 2, LocalDateTime.now().plusDays(12), accountManager.find(1));
            loanManager.createLoan(3, 3, LocalDateTime.now().plusDays(5), accountManager.find(2));
            Assertions.assertEquals(loanManager.find(1).getPercentage(), 2);
            loanManager.update(1, "percentage", 100);
            Assertions.assertEquals(loanManager.find(1).getPercentage(), 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loanManager.deleteAll();
        end();
    }
}
