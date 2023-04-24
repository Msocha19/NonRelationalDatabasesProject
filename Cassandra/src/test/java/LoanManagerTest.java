import com.datastax.oss.driver.api.core.CqlSession;
import managers.AccountManager;
import managers.ClientManager;
import managers.LoanManager;
import model.actions.Loan;
import org.junit.jupiter.api.*;
import repository.cassandra.CassandraClient;

import java.time.LocalDate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanManagerTest {

    private CqlSession session;
    private LoanManager loanManager;
    private AccountManager accountManager;
    private ClientManager clientManager;

    @BeforeAll
    public void init() {
        CassandraClient cassandraClient = new CassandraClient();
        this.session = cassandraClient.initSession();
        this.loanManager = new LoanManager(session);
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
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(1));
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(2));
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(3));
    }

    private void create() {
        this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20");
        this.clientManager.createPerson("France", "Mateusz", "Sochacki", "236652", 3000, "533 998 311", "Paris", "av. Saint Jean", "3");
        this.clientManager.createPerson("Westeros", "Jon", "Snow", "230785", 4000, "533 998 311", "CastleBlack", "street", "51");
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(1, 12, "Normal", 1));
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(2, 5.5, "Normal", 2));
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(3, 7.8, "Savings", 3));
        this.loanManager.create(LocalDate.of(2023, 12, 23), 1, 2000d, 12.5);
        this.loanManager.create(LocalDate.of(2024, 12, 23), 2, 3000d, 6.5);
        this.loanManager.create(LocalDate.of(2025, 12, 23), 3, 5000d, 1.5);
    }

    @Test
    @Order(1)
    public void createTest() {
        this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20");
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(1, 12, "Normal", 1));
        Assertions.assertEquals(0, this.accountManager.getAccount(1).getBalance());
        Assertions.assertDoesNotThrow(() -> this.loanManager.create(LocalDate.of(2023, 12, 23), 1, 2000d, 12.5));
        Assertions.assertEquals(2000, this.accountManager.getAccount(1).getBalance());
        Assertions.assertThrows(Exception.class ,() -> this.loanManager.create(LocalDate.of(2021, 12, 23), 1, 2000d, 12.5));
        Assertions.assertThrows(Exception.class ,() -> this.loanManager.create(LocalDate.of(2023, 12, 23), 1, -3d, 12.5));
        Assertions.assertThrows(Exception.class ,() -> this.loanManager.create(LocalDate.of(2023, 12, 23), 12, 2000d, 12.5));
        Assertions.assertThrows(Exception.class ,() -> this.loanManager.create(LocalDate.of(2023, 12, 23), 1, 2000d, -4d));
        this.clientManager.deleteClient(1);
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(1));
    }

    @Test
    @Order(2)
    public void deleteTest() {
        this.create();
        Loan loan = this.loanManager.getLoan(1);
        loan.setPaidAmount(1000);
        this.loanManager.update(loan);
        Assertions.assertEquals(2000, this.accountManager.getAccount(1).getBalance());
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(1));
        Assertions.assertEquals(1000, this.accountManager.getAccount(1).getBalance());
        Assertions.assertEquals(2, this.loanManager.getAll(null).size());
        Assertions.assertThrows(Exception.class,() -> this.loanManager.getLoan(1));
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(2));
        Assertions.assertDoesNotThrow(() -> this.loanManager.delete(3));
        this.clientManager.deleteClient(1);
        this.clientManager.deleteClient(2);
        this.clientManager.deleteClient(3);
    }

    @Test
    @Order(3)
    public void readTest() {
        this.create();
        Assertions.assertEquals(12.5, this.loanManager.getLoan(1).getPercentage());
        Assertions.assertEquals(3, this.loanManager.getAll(null).size());
        this.loanManager.setArchive(1);
        Assertions.assertEquals(2, this.loanManager.getAll(false).size());
        Assertions.assertEquals(1, this.loanManager.getAll(true).size());
        Assertions.assertEquals(12.5, this.loanManager.findLoanByAccount(1).getPercentage());
//        Assertions.assertEquals(6.5, this.loanManager.getLoansFromRange(LocalDate.of(2024, 01, 12), LocalDate.of(2025, 01, 01)).get(0).getPercentage());
        Assertions.assertThrows(Exception.class, () -> this.loanManager.getLoan(14141324));
        this.delete();
    }

    @Test
    @Order(4)
    public void updateTest() {
        this.create();
        Loan loan = this.loanManager.getLoan(1);
        Assertions.assertEquals(12.5,  loan.getPercentage());
        Assertions.assertEquals(2000d, loan.getInitAmount());
        loan.setPercentage(50d);
        loan.setInitAmount(4000d);
        Assertions.assertDoesNotThrow(() -> this.loanManager.update(loan));
        Loan updated = this.loanManager.getLoan(1);
        Assertions.assertEquals(50d, updated.getPercentage());
        Assertions.assertEquals(4000d, updated.getInitAmount());
        this.delete();
    }
}
