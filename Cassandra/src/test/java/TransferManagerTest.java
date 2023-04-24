import com.datastax.oss.driver.api.core.CqlSession;
import managers.AccountManager;
import managers.ClientManager;
import managers.TransferManager;
import model.actions.Transfer;
import org.junit.jupiter.api.*;
import repository.cassandra.CassandraClient;

import java.time.LocalDate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferManagerTest {
    private CqlSession session;
    private TransferManager transferManager;
    private AccountManager accountManager;
    private ClientManager clientManager;

    @BeforeAll
    public void init() {
        CassandraClient cassandraClient = new CassandraClient();
        this.session = cassandraClient.initSession();
        this.transferManager = new TransferManager(session);
        this.accountManager = new AccountManager(session);
        this.clientManager = new ClientManager(session);
    }

    @AfterAll
    public void close() {
        this.session.close();
    }

    private void delete() {
        Assertions.assertDoesNotThrow(() -> this.clientManager.deleteClient(1));
        Assertions.assertDoesNotThrow(() -> this.clientManager.deleteClient(2));
        Assertions.assertDoesNotThrow(() -> this.clientManager.deleteClient(3));
        Assertions.assertDoesNotThrow(() -> this.transferManager.delete(1));
        Assertions.assertDoesNotThrow(() -> this.transferManager.delete(2));
        Assertions.assertDoesNotThrow(() -> this.transferManager.delete(3));
    }

    private void create() {
        this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20");
        this.clientManager.createPerson("France", "Mateusz", "Sochacki", "236652", 3000, "533 998 311", "Paris", "av. Saint Jean", "3");
        this.clientManager.createPerson("Westeros", "Jon", "Snow", "230785", 4000, "533 998 311", "CastleBlack", "street", "51");
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(1, 12, "Normal", 1));
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(2, 5.5, "Normal", 2));
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(3, 7.8, "Savings", 3));
        this.accountManager.deposit(1, 10000);
        this.accountManager.deposit(2, 10000);
        this.accountManager.deposit(3, 10000);
        Assertions.assertDoesNotThrow(() -> this.transferManager.create(1, 2, 2000d, LocalDate.now()));
        Assertions.assertDoesNotThrow(() -> this.transferManager.create(2, 3, 1000d, LocalDate.now()));
        Assertions.assertDoesNotThrow(() -> this.transferManager.create(1, 3, 500d, LocalDate.now()));
    }

    @Test
    @Order(1)
    public void createTest() {
        this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20");
        this.clientManager.createPerson("France", "Mateusz", "Sochacki", "236652", 3000, "533 998 311", "Paris", "av. Saint Jean", "3");
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(1, 12, "Normal", 1));
        this.accountManager.deposit(1, 10000);
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(2, 5.5, "Normal", 2));
        this.accountManager.deposit(2, 10000);
        Assertions.assertEquals(10000, this.accountManager.getAccount(1).getBalance());
        Assertions.assertEquals(10000, this.accountManager.getAccount(2).getBalance());
        Assertions.assertDoesNotThrow(() -> this.transferManager.create(1, 2, 2000d, LocalDate.now()));
        Assertions.assertEquals(8000, this.accountManager.getAccount(1).getBalance());
        Assertions.assertEquals(12000, this.accountManager.getAccount(2).getBalance());
        Assertions.assertEquals(2000d, this.transferManager.getTransfer(1).getAmount());
        Assertions.assertEquals(1, this.transferManager.getTransfer(1).getFromAccount());
        this.clientManager.deleteClient(1);
        this.clientManager.deleteClient(2);
        this.transferManager.delete(1);
    }

    @Test
    @Order(2)
    public void deleteTest() {
        this.create();
        Assertions.assertEquals(2000, this.transferManager.getTransfer(1).getAmount());
        Assertions.assertEquals(3, this.transferManager.getAll().size());
        Assertions.assertDoesNotThrow(() -> this.transferManager.delete(1));
        Assertions.assertEquals(2, this.transferManager.getAll().size());
        Assertions.assertThrows(Exception.class, () -> this.transferManager.getTransfer(1));
        this.clientManager.deleteClient(1);
        this.clientManager.deleteClient(2);
        this.clientManager.deleteClient(3);
        Assertions.assertDoesNotThrow(() -> this.transferManager.delete(2));
        Assertions.assertDoesNotThrow(() -> this.transferManager.delete(3));
        Assertions.assertEquals(0, this.transferManager.getAll().size());
    }

    @Test
    @Order(3)
    public void readTest() {
        this.create();
        Assertions.assertEquals(2000, this.transferManager.getTransfer(1).getAmount());
        Assertions.assertEquals(2, this.transferManager.findByAccountSender(1).size());
        Assertions.assertEquals(2, this.transferManager.findByAccountReceiver(3).size());
        Assertions.assertEquals(1, this.transferManager.findByAccountReceiver(2).size());
        Assertions.assertEquals(1, this.transferManager.findByAccountSender(2).size());
        Assertions.assertEquals(3, this.transferManager.getAll().size());
        Assertions.assertThrows(Exception.class, () -> this.transferManager.getTransfer(21414));
        this.delete();
    }

    @Test
    @Order(4)
    public void updateTest() {
        this.create();
        Transfer transfer = this.transferManager.getTransfer(1);
        Assertions.assertEquals(2000, transfer.getAmount());
        Assertions.assertEquals(2, transfer.getToAccount());
        transfer.setAmount(1000);
        transfer.setToAccount(3);
        Assertions.assertDoesNotThrow(() -> this.transferManager.update(transfer));
        Transfer updated = this.transferManager.getTransfer(1);
        Assertions.assertEquals(1000, updated.getAmount());
        Assertions.assertEquals(3, updated.getToAccount());
        this.delete();
    }
}
