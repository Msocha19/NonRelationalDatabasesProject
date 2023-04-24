package repository;

import actions.Transfer;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import managers.AccountManager;
import managers.ClientManager;
import managers.TransferManager;
import model.AccountType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CacheableTransferRepositoryTests {

    private AccountManager accountManager;
    private ClientManager clientManager;
    private TransferManager transferManager;
    private Jsonb jsonb;

    public CacheableTransferRepositoryTests() {
        accountManager = new AccountManager();
        clientManager = new ClientManager();
        transferManager = new TransferManager();
        jsonb = JsonbBuilder.create();
    }

    @BeforeEach public void init() throws Exception {
        clientManager.deleteAll();
        accountManager.deleteAll();
        transferManager.deleteAll();

        clientManager.createCompany(1, 2000, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
        clientManager.createPerson(2, 1240, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");
        clientManager.createCompany(3, 3560, "+48 533998311", "Poland", "Lodz", "al.Politechniki", "33", "Politechnika Lodzka", "123");
        clientManager.createPerson(4, 2435345, "1234", "Poland", "Lodz", "al.Politechniki", "24", "Mateusz", "Sochacki", "236652");

        accountManager.createAccount(1, 1.2, AccountType.Normal, clientManager.find(1));
        accountManager.createAccount(2, 1.3, AccountType.Normal, clientManager.find(2));
        accountManager.createAccount(3, 1.4, AccountType.Normal, clientManager.find(3));
        accountManager.createAccount(4, 1.5, AccountType.Normal, clientManager.find(4));
        accountManager.deposit(10000, 1);
        accountManager.deposit(10000, 2);
        accountManager.deposit(10000, 3);
        accountManager.deposit(10000, 4);
    }

    @Test public void addTest() {
        CacheableTransferRepository repository = new CacheableTransferRepository();
        JedisPool pool = RedisConnection.create();

        Assertions.assertNotNull(repository);
        Assertions.assertEquals(new ArrayList<>(), repository.getAll());

        try (Jedis jedis = pool.getResource()) {
            Transfer transfer = new Transfer(1,
                    accountManager.find(1),
                    accountManager.find(2),
                    100,
                    LocalDateTime.now().plusDays(1));

            repository.add(transfer);

            String transferJson = jedis.get(Transfer.class.getName() + "1");
            Transfer transferRaw = jsonb.fromJson(transferJson, Transfer.class);
            Assertions.assertEquals(transfer, transferRaw);
        }

        pool.close();
    }

    @Test public void cleanCacheTest() {
        CacheableTransferRepository repository = new CacheableTransferRepository();
        JedisPool pool = RedisConnection.create();

        Assertions.assertNotNull(repository);
        Assertions.assertEquals(new ArrayList<>(), repository.getAll());

        Transfer transfer0 = new Transfer(1,
                accountManager.find(1),
                accountManager.find(2),
                100,
                LocalDateTime.now().plusDays(1));

        Transfer transfer1 = new Transfer(2,
                accountManager.find(2),
                accountManager.find(1),
                100,
                LocalDateTime.now().plusDays(1));

        repository.add(transfer0);
        repository.add(transfer1);
        List<Transfer> res = repository.getAll();
        Assertions.assertEquals(transfer0, res.get(0));
        Assertions.assertEquals(transfer1, res.get(1));

        repository.wipeAll();

        Assertions.assertNotNull(repository);
        Assertions.assertEquals(new ArrayList<>(), repository.getAll());
        Assertions.assertEquals(0, repository.getAll().size());

        pool.close();
    }

    @Test public void getTest() {
        CacheableTransferRepository repository = new CacheableTransferRepository();
        JedisPool pool = RedisConnection.create();

        Assertions.assertNotNull(repository);
        Assertions.assertEquals(new ArrayList<>(), repository.getAll());

        try (Jedis jedis = pool.getResource()) {
            Transfer transfer = new Transfer(1,
                    accountManager.find(1),
                    accountManager.find(2),
                    100,
                    LocalDateTime.now().plusDays(1));

            repository.add(transfer);
            Transfer transfer1 = repository.find("_transferId", 1);
            Assertions.assertEquals(transfer, transfer1);

            String transferJson = jedis.get(Transfer.class.getName() + "1");
            Transfer t1 = jsonb.fromJson(transferJson, Transfer.class);
            Assertions.assertEquals(transfer, t1);
        }

        pool.close();
    }

    @Test void expireTest() throws InterruptedException {
        CacheableTransferRepository repository = new CacheableTransferRepository();
        JedisPool pool = RedisConnection.create();

        Assertions.assertNotNull(repository);
        Assertions.assertEquals(new ArrayList<>(), repository.getAll());

        try (Jedis jedis = pool.getResource()) {
            Transfer transfer = new Transfer(1,
                    accountManager.find(1),
                    accountManager.find(2),
                    100,
                    LocalDateTime.now().plusDays(1));

            repository.add(transfer);
            Transfer transfer1 = repository.find("_transferId", 1);
            Assertions.assertEquals(transfer, transfer1);

            Thread.sleep(10000L);
            String transferJson = jedis.get(Transfer.class.getName() + "1");
            Assertions.assertNull(transferJson);

            Transfer transferFromDB = repository.find("_transferId", 1);
            Assertions.assertNotNull(transferFromDB);
        }

        pool.close();
    }


    @Test public void connErrorTest() throws InterruptedException {
        CacheableTransferRepository repository = new CacheableTransferRepository();
        JedisPool pool = RedisConnection.create();

        Assertions.assertNotNull(repository);
        Assertions.assertEquals(new ArrayList<>(), repository.getAll());

        try (Jedis jedis = pool.getResource()) {
            Transfer transfer = new Transfer(1,
                    accountManager.find(1),
                    accountManager.find(2),
                    100,
                    LocalDateTime.now().plusDays(1));

            repository.add(transfer);
            Transfer transfer1 = repository.find("_transferId", 1);
            Assertions.assertEquals(transfer, transfer1);

            System.out.println("SHUTDOWN DOCKER NOW!!");
            Thread.sleep(5000L);

            Assertions.assertThrows(JedisConnectionException.class, () -> {
                String transferJson = jedis.get(Transfer.class.getName() + "1");
                Assertions.assertNull(transferJson);
            });


            Transfer transferFromDB = repository.find("_transferId", 1);
            Assertions.assertNotNull(transferFromDB);
            Assertions.assertEquals(transferFromDB, transfer);
            Assertions.assertEquals(transferFromDB, transfer1);
        }

        pool.close();
    }
}
