import com.datastax.oss.driver.api.core.CqlSession;
import managers.AccountManager;
import managers.ClientManager;
import org.junit.jupiter.api.*;
import repository.cassandra.CassandraClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountManagerTest {

    private CqlSession session;
    private ClientManager clientManager;
    private AccountManager accountManager;

    @BeforeAll
    public void init() {
        CassandraClient cassandraClient = new CassandraClient();
        this.session = cassandraClient.initSession();
        this.clientManager = new ClientManager(session);
        this.accountManager = new AccountManager(session);
    }

    @AfterAll
    public void close() {
        this.session.close();
    }

    private void delete() {
        this.clientManager.deleteClient(1);
        this.clientManager.deleteClient(2);
        this.clientManager.deleteClient(3);
    }

    private void create() {
        this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20");
        this.clientManager.createPerson("France", "Mateusz", "Sochacki", "236652", 3000, "533 998 311", "Paris", "av. Saint Jean", "3");
        this.clientManager.createPerson("Westeros", "Jon", "Snow", "230785", 4000, "533 998 311", "CastleBlack", "street", "51");
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(1, 12, "Normal", 1));
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(2, 5.5, "Normal", 2));
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(3, 7.8, "Savings", 3));
    }

    @Test
    @Order(1)
    public void createTest() {
        this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20");
        this.clientManager.createPerson("France", "Mateusz", "Sochacki", "236652", 3000, "533 998 311", "Paris", "av. Saint Jean", "3");
        this.clientManager.createPerson("Westeros", "Jon", "Snow", "230785", 4000, "533 998 311", "CastleBlack", "street", "51");
        Assertions.assertEquals(0, this.clientManager.getClientById(1).getAccounts().size());
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(1, 12, "Normal", 1));
        Assertions.assertEquals(1, this.accountManager.getClientByAccount(1).getId());
        Assertions.assertEquals("Normal", this.accountManager.getAccount(1).getAccountType());
        Assertions.assertThrows(Exception.class, () -> this.accountManager.create(1, 43, "Normal", 2));
        Assertions.assertThrows(Exception.class, () -> this.accountManager.create(3, 43, "Normal", 1234567));
        Assertions.assertThrows(Exception.class, () -> this.accountManager.create(2, 43, "Unsupported", 3));
        Assertions.assertThrows(Exception.class, () -> this.accountManager.create(4, -3, "Normal", 2));
        Assertions.assertEquals(1, this.clientManager.getClientById(1).getAccounts().size());
        this.clientManager.deleteClient(1);
        this.clientManager.deleteClient(2);
        this.clientManager.deleteClient(3);
    }

    @Test
    @Order(1)
    public void deleteTest() {
        this.create();
        Assertions.assertDoesNotThrow(() -> this.accountManager.delete(1));
        Assertions.assertEquals(2, this.accountManager.getAll().size());
        Assertions.assertThrows(Exception.class, () -> this.accountManager.getAccount(1));
        Assertions.assertEquals(0, this.clientManager.getClientById(1).getAccounts().size());
        Assertions.assertThrows(Exception.class, () -> this.accountManager.delete(1));
        this.clientManager.deleteClient(2);
        Assertions.assertThrows(Exception.class, () -> this.accountManager.getAccount(2));
        Assertions.assertEquals(1, this.accountManager.getAll().size());
        this.clientManager.deleteClient(3);
        this.clientManager.deleteClient(1);
    }

    @Test
    @Order(1)
    public void readTest() {
        this.create();
        Assertions.assertEquals(3, this.accountManager.getAll().size());
        Assertions.assertEquals("Savings", this.accountManager.getAccount(3).getAccountType());
        Assertions.assertEquals(1, this.accountManager.getAccountsByType("Savings").size());
        Assertions.assertEquals(2, this.accountManager.getAccountsByType("Normal").size());
        Assertions.assertEquals("Savings", this.accountManager.findByClient(3).get(0).getAccountType());
        Assertions.assertEquals("Westeros", this.accountManager.getClientByAccount(3).getCountry());
        Assertions.assertThrows(Exception.class, () -> this.accountManager.getAccountsByType("Unsupported"));
        Assertions.assertThrows(Exception.class, () -> this.accountManager.getAccount(12341241));
        Assertions.assertThrows(Exception.class, () -> this.accountManager.getClientByAccount(34));
        Assertions.assertThrows(Exception.class, () -> this.accountManager.findByClient(4));
        this.delete();
    }

    @Test
    @Order(1)
    public void updateTest() {
        this.create();
        Assertions.assertEquals(12, this.accountManager.getAccount(1).getPercentage());
        Assertions.assertEquals(1, this.accountManager.getClientByAccount(1).getId());
        Assertions.assertEquals(1, this.clientManager.getClientById(1).getAccounts().size());
        Assertions.assertEquals(1, this.clientManager.getClientById(2).getAccounts().size());
        Assertions.assertDoesNotThrow(()-> this.accountManager.updateAccount(1,-1d, 7d, 2, null));
        Assertions.assertEquals(7, this.accountManager.getAccount(1).getPercentage());
        Assertions.assertEquals(2, this.accountManager.getClientByAccount(1).getId());
        Assertions.assertEquals(0, this.clientManager.getClientById(1).getAccounts().size());
        Assertions.assertEquals(2, this.clientManager.getClientById(2).getAccounts().size());
        this.delete();
    }
}
