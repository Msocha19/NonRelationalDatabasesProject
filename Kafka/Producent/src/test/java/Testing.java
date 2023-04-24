import com.fasterxml.jackson.databind.JsonMappingException;
import kafka.ProducerKafka;
import managers.AccountManager;
import managers.ClientManager;
import managers.TransferManager;
import model.AccountType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class Testing {
    private static AccountManager accountManager;
    private static ClientManager clientManager;
    private static TransferManager transferManager;

    public Testing() {
        accountManager = new AccountManager();
        clientManager = new ClientManager();
        transferManager = new TransferManager();
    }

    @Test
    public void test() {
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
            accountManager.deposit(200, 1);
            accountManager.deposit(200, 2);
            accountManager.deposit(200, 3);
            accountManager.deposit(200, 4);
            transferManager.createTranfer(1, 2,2, LocalDateTime.now());
            transferManager.createTranfer(2, 1,2, LocalDateTime.now());
            transferManager.createTranfer(3, 2,2, LocalDateTime.now());
            transferManager.createTranfer(4, 2,2, LocalDateTime.now());
            transferManager.createTranfer(4, 1,2, LocalDateTime.now());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
