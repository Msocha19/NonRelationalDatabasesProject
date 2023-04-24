package managers;

import model.AccountType;
import model.Company;
import model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClientManagerTest {

    private final ClientManager clientManager;

    public ClientManagerTest(){
        this.clientManager = new ClientManager();
    }

    @BeforeAll static void clean() {
        new ClientManager().deleteAll();
    }
    @Test
    public void createTest() {
        Assertions.assertDoesNotThrow(() ->clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123"));
        Assertions.assertDoesNotThrow(() ->clientManager.createPerson(2, 1240, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652"));
        Assertions.assertEquals(clientManager.find(1).getClass(), Company.class);
        Assertions.assertEquals(clientManager.find(1).getIncome(), 2000);
        Assertions.assertEquals(clientManager.find(2).getClass(), Person.class);
        Assertions.assertEquals(clientManager.find(2).getIncome(), 1240);
        Assertions.assertThrows(Exception.class,()-> clientManager.addClient(clientManager.find(1)));
        Assertions.assertDoesNotThrow(() ->clientManager.deleteClient(1));
        Assertions.assertDoesNotThrow(() ->clientManager.deleteClient(2));

    }

    @Test
    public void deleteTest() {
        Assertions.assertDoesNotThrow(() ->clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123"));
        Assertions.assertEquals(clientManager.find(1).getIncome(), 2000);
        Assertions.assertEquals(clientManager.findAll().size(), 1);
        Assertions.assertDoesNotThrow(() ->clientManager.deleteClient(1));
        Assertions.assertEquals(clientManager.findAll().size(), 0);
        Assertions.assertThrows(Exception.class, ()-> clientManager.deleteClient(1));
    }

    @Test
    public void findTest() {
        Assertions.assertDoesNotThrow(() ->clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123"));
        Assertions.assertDoesNotThrow(() ->clientManager.createPerson(2, 1240, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652"));
        Assertions.assertEquals(clientManager.findAll().size(), 2);
        Assertions.assertEquals(clientManager.getAllCompanies().get(0).getId(), 1);
        Assertions.assertEquals(clientManager.getAllPersons().get(0).getId(), 2);
        Assertions.assertEquals(clientManager.find(1).getClass(), Company.class);
        Assertions.assertEquals(clientManager.find(2).getCity(), "Lodz");
        Assertions.assertDoesNotThrow(() ->clientManager.deleteClient(1));
        Assertions.assertDoesNotThrow(() ->clientManager.deleteClient(2));
    }

    @Test
    public void updateTest() {
        try {
            AccountManager accountManager = new AccountManager();
            clientManager.deleteClient(1);
            clientManager.deleteClient(2);
            accountManager.deleteAccount(1);
            accountManager.deleteAccount(2);
            accountManager.deleteAccount(3);
            clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
            clientManager.createPerson(2, 1240, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");
            accountManager.createAccount(1, 2, AccountType.Normal, clientManager.find(1));
            accountManager.createAccount(2, 2, AccountType.Normal, clientManager.find(1));
            accountManager.createAccount(3, 2, AccountType.Normal, clientManager.find(2));
            Assertions.assertEquals(clientManager.find(1).getAccounts().size(), 2);
            Assertions.assertEquals(accountManager.getClient(1).getIncome(), 2000);
            clientManager.update(1, "income", 10000);
            Assertions.assertThrows(Exception.class, ()-> clientManager.update(4,"income", 30));
            Assertions.assertEquals(accountManager.getClient(1).getIncome(), 10000);
            Assertions.assertEquals(clientManager.find(1).getIncome(), 10000);
            Assertions.assertEquals(accountManager.findAll().size(), 3);
            clientManager.deleteClient(1);
            clientManager.deleteClient(2);
            Assertions.assertEquals(accountManager.findAll().size(), 0);
        } catch (Exception e) {
          //  e.printStackTrace();
        }
    }
}
