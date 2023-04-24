import com.datastax.oss.driver.api.core.CqlSession;
import managers.AccountManager;
import managers.ClientManager;
import model.Client;
import model.Company;
import model.Person;
import org.junit.jupiter.api.*;
import repository.cassandra.CassandraClient;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientManagerTest {

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
    }

    @Test
    @Order(1)
    public void clientCreateTest() {
        Assertions.assertDoesNotThrow(()-> this.clientManager.createCompany("Poland", "TomTom", "123", 256567, "42 211 31 39", "Lodz", "Piotrkowska", "20"));
        Assertions.assertDoesNotThrow(()-> this.clientManager.createPerson("France", "Mateusz", "Sochacki", "236652", 3000, "533 998 311", "Paris", "av. Saint Jean", "3"));
        Assertions.assertDoesNotThrow(()-> this.clientManager.createPerson("Westeros", "Jon", "Snow", "230785", 4000, "533 998 311", "CastleBlack", "street", "51"));
        Assertions.assertEquals(3, this.clientManager.getAll().size());
        this.delete();
    }

    @Test
    @Order(2)
    public void clientReadTest() {
        this.create();
        Assertions.assertEquals(3, this.clientManager.getAll().size());
        Client client = this.clientManager.getClientById(1);
        Company company = (Company)client;
        Assertions.assertEquals(Company.class, client.getClass());
        Assertions.assertEquals("TomTom", company.getName());
        Assertions.assertEquals("123", company.getNIP());
        Assertions.assertEquals("Poland", company.getCountry());
        client = this.clientManager.getClientById(2);
        Person person = (Person) client;
        Assertions.assertEquals(Person.class, client.getClass());
        Assertions.assertEquals("Mateusz", person.getName());
        Assertions.assertEquals("Sochacki", person.getSurname());
        Assertions.assertEquals("France", person.getCountry());
        List<Client> people = this.clientManager.getPeople();
        List<Client> companies = this.clientManager.getCompanies();
        Assertions.assertEquals(people.get(1).getCountry(), this.clientManager.getClientById(3).getCountry());
        Assertions.assertEquals(companies.get(0).getCountry(), this.clientManager.getClientById(1).getCountry());
        Assertions.assertEquals(1, this.clientManager.getCompanies().size());
        Assertions.assertEquals(2, this.clientManager.getPeople().size());
        this.delete();
    }

    @Test
    @Order(3)
    public void clientDeleteTest() {
        this.create();
        Assertions.assertDoesNotThrow(()-> this.clientManager.deleteClient(1));
        Assertions.assertEquals(2, this.clientManager.getAll().size());
        Assertions.assertThrows(Exception.class, ()-> this.clientManager.deleteClient(1));
        Assertions.assertDoesNotThrow(()-> this.clientManager.deleteClient(2));
        Assertions.assertEquals(1, this.clientManager.getAll().size());
        Assertions.assertThrows(Exception.class, ()-> this.clientManager.deleteClient(2));
        Assertions.assertDoesNotThrow(()-> this.clientManager.deleteClient(3));
        Assertions.assertEquals(0, this.clientManager.getAll().size());
        Assertions.assertThrows(Exception.class, ()-> this.clientManager.deleteClient(3));
    }

    @Test
    @Order(4)
    public void openAccountTest() {
        this.create();
        Assertions.assertEquals(this.clientManager.getClientById(1).getAccounts().size(), 0);
        Assertions.assertDoesNotThrow(() -> this.accountManager.create(123, 12, "Normal", 1));
        Assertions.assertEquals(this.clientManager.getClientById(1).getAccounts().size(), 1);
        Assertions.assertEquals(this.clientManager.getClientById(1).getAccounts().get(0), 123);
        this.delete();
    }

    @Test
    @Order(5)
    public void updateClientTest() {
        this.create();
        Company client = (Company) this.clientManager.getClientById(1);
        Assertions.assertEquals("Poland", client.getCountry());
        Assertions.assertEquals("TomTom", client.getName());
        Assertions.assertEquals("123", client.getNIP());
        Assertions.assertDoesNotThrow(() ->this.clientManager.updateCompany(1, 0, null, "Germany", null, null, null, "NewName", null));
        Company updatedClient = (Company) this.clientManager.getClientById(1);
        Assertions.assertEquals("Germany", updatedClient.getCountry());
        Assertions.assertEquals("NewName", updatedClient.getName());
        Assertions.assertEquals("123", updatedClient.getNIP());
        this.delete();
    }
}
