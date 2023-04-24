package managers;

import model.AccountType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TransferManagerTest {

    private static AccountManager accountManager;
    private static ClientManager clientManager;
    private static TransferManager transferManager;

    public TransferManagerTest() {
        accountManager = new AccountManager();
        clientManager = new ClientManager();
        transferManager = new TransferManager();
    }

    public void innit() {
        try {
            clientManager.deleteAll();
            accountManager.deleteAll();
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
            transferManager.deleteAll();
        } catch (Exception e) {
          //  e.printStackTrace();
        }
    }

    @BeforeEach void ending() {
        end();
        innit();
        transferManager.deleteAll();
    }

    @Test
    public void createTest() {
        accountManager.deposit(200, 1);
        accountManager.deposit(200, 2);
        Assertions.assertDoesNotThrow(() -> transferManager.createTranfer(1, 2, 20, LocalDateTime.now().plusDays(20)));
        Assertions.assertEquals(transferManager.findAll().size(), 1);
        Assertions.assertEquals(transferManager.find(1).getAmount(), 20);
        Assertions.assertEquals(accountManager.find(1).getBalance(), 180);
        Assertions.assertEquals(accountManager.find(2).getBalance(), 220);
        transferManager.deleteAll();
    }

    @Test
    public void deleteTest() {
        accountManager.deposit(200, 1);
        accountManager.deposit(200, 2);
        accountManager.deposit(200, 3);
        Assertions.assertDoesNotThrow(() ->transferManager.createTranfer(1, 2, 20, LocalDateTime.now().plusDays(20)));
        Assertions.assertDoesNotThrow(() ->transferManager.createTranfer(2, 3, 20, LocalDateTime.now().plusDays(1)));
        Assertions.assertEquals(transferManager.findAll().size(), 2);
        try {
            transferManager.deleteTranferRecord(1);
            Assertions.assertEquals(transferManager.findAll().size(), 1);
            Assertions.assertThrows(Exception.class,()-> transferManager.deleteTranferRecord(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        transferManager.deleteAll();
        Assertions.assertEquals(transferManager.findAll().size(), 0);
    }

    @Test
    public void findTest() {
        accountManager.deposit(200, 1);
        accountManager.deposit(200, 2);
        accountManager.deposit(200, 3);
        Assertions.assertDoesNotThrow(() ->transferManager.createTranfer(1, 2, 20, LocalDateTime.now().plusDays(20)));
        Assertions.assertDoesNotThrow(() ->transferManager.createTranfer(2, 3, 10, LocalDateTime.now().plusDays(1)));
        Assertions.assertEquals(transferManager.find(1).getAmount(), 20);
        Assertions.assertEquals(transferManager.find(2).getAmount(), 10);
        Assertions.assertEquals(transferManager.findFromAccount(1).getPercentage(), 1.2);
        Assertions.assertEquals(transferManager.findToAccount(1).getPercentage(), 1.3);
        transferManager.deleteAll();
    }

    @Test
    public void manualTest() {
        accountManager.deposit(200, 1);
        accountManager.deposit(200, 2);
        Assertions.assertDoesNotThrow(() ->transferManager.createTranfer(1, 2, 20, LocalDateTime.now().plusDays(20)));
    }
}
