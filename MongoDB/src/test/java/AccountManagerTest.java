import managers.AccountManager;
import managers.ClientManager;
import model.Account;
import model.AccountType;
import model.Company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountManagerTest {

    private static AccountManager accountManager;
    private static ClientManager clientManager;

    public AccountManagerTest() {
        accountManager = new AccountManager();
        clientManager = new ClientManager();
    }

    public void innit() {
        try {
            clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
            clientManager.createPerson(2, 1240, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");
            clientManager.createCompany(3, 3560, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
            clientManager.createPerson(4, 2435345, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void end() {
        try {
            clientManager.deleteAll();
            accountManager.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createTest() {
        end();
        innit();
        try {
            Assertions.assertEquals(clientManager.find(1).getAccounts().size(), 0);
            accountManager.createAccount(1, 1.2, AccountType.Normal, clientManager.find(1));
            accountManager.createAccount(2, 1.2, AccountType.Savings, clientManager.find(2));
            Assertions.assertEquals(clientManager.find(1).getAccounts().size(), 1);
            Assertions.assertEquals(accountManager.findAll().size(), 2);
            Assertions.assertEquals(accountManager.find(1).getPercentage(), 1.2);
            Assertions.assertThrows(Exception.class, () -> accountManager.createAccount(1, 2, AccountType.Normal, clientManager.find(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
       end();
    }

    @Test
    public void deleteTest() {
        end();
        innit();
        try {
            accountManager.createAccount(1, 1.2, AccountType.Normal, clientManager.find(1));
            accountManager.createAccount(2, 1.2, AccountType.Savings, clientManager.find(2));
            Assertions.assertEquals(accountManager.findAll().size(), 2);
            accountManager.deleteAccount(1);
            Assertions.assertEquals(accountManager.findAll().size(), 1);
            Assertions.assertEquals(clientManager.find(1).getAccounts().size(), 0);
            Assertions.assertThrows(Exception.class, ()->accountManager.deleteAccount(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        end();
    }

    @Test
    public void findTest() {
        end();
        innit();
        try {
            accountManager.createAccount(2, 1.2, AccountType.Savings, clientManager.find(2));
            accountManager.createAccount(3, 2, AccountType.Savings, clientManager.find(1));
            Assertions.assertEquals(accountManager.getClient(3).getId(), 1);
            Assertions.assertEquals(accountManager.getClient(3).getClass(), Company.class);
            Assertions.assertEquals(accountManager.find(2).getAccountType(), AccountType.Savings);
            Assertions.assertEquals(accountManager.findAll().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        end();
    }

    @Test
    public void updateTest() {
        end();
        innit();
        try {
            accountManager.createAccount(3, 2, AccountType.Savings, clientManager.find(1));
            Assertions.assertEquals(accountManager.getClient(3).getAccounts().get(0), 3);
            accountManager.update(3, "_number", 4);
            Assertions.assertEquals(accountManager.getClient(4).getAccounts().get(0), 4);
            Assertions.assertEquals(accountManager.find(4).getPercentage(), 2);
            Assertions.assertEquals(clientManager.find(1).getAccounts().get(0), 4);
            accountManager.update(4, "percentage", 5.5);
            Assertions.assertEquals(accountManager.find(4).getPercentage(), 5.5);
            Assertions.assertThrows(Exception.class, ()->accountManager.update(10, "percentage", 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        end();
    }

    @Test
    public void depositWithdrawTest() {
        end();
        innit();
        try {
            accountManager.createAccount(3, 2, AccountType.Savings, clientManager.find(1));
            accountManager.deposit(1000, 3);
            Assertions.assertEquals(accountManager.find(3).getBalance(), 1000);
            accountManager.withdraw(500, 3);
            Assertions.assertEquals(accountManager.find(3).getBalance(), 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        end();
    }
}
