package redis;

import actions.Transfer;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import model.Account;
import model.AccountType;
import model.ClientAddress;
import model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.time.LocalDateTime;

class RedisConnTests {

    private static JedisPool pool;
    private static Jsonb jsonb;

    @BeforeEach void create() {
        pool = RedisConnection.create();
        jsonb = JsonbBuilder.create();
    }

    @Test
    void smokeTest() {
        try (Jedis jedis = pool.getResource()) {
            Assertions.assertNotNull(jedis);

            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("OK", jedis.set("k2", "wartosc"));

            String w1 = jedis.get("k1");
            String w2 = jedis.get("k2");
            String w3 = jedis.get("k1");
            Assertions.assertEquals(w1, w3);
            Assertions.assertEquals("wartosc", w1);
            Assertions.assertEquals("wartosc", w2);
            Assertions.assertEquals("wartosc", w3);

            Assertions.assertNull(jedis.get("invalidkey"));
        }
    }

    @Test
    void closedConnectionTest() {
        try (Jedis jedis = pool.getResource()) {
            Assertions.assertNotNull(jedis);
            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("wartosc", jedis.get("k1"));
        }
        pool.close();
        Assertions.assertNotNull(pool);
        Assertions.assertTrue(pool.isClosed());
        Assertions.assertThrows(JedisException.class, () -> pool.getResource());

        pool = RedisConnection.create();
        Assertions.assertNotNull(pool);
        Assertions.assertFalse(pool.isClosed());

        try (Jedis jedis = pool.getResource()) {
            Assertions.assertNotNull(jedis);
            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("wartosc", jedis.get("k1"));
        }
    }

    @Test
    void closePoolDuringTry() {
        try (Jedis jedis = pool.getResource()) {
            Assertions.assertNotNull(jedis);
            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("wartosc", jedis.get("k1"));

            pool.close();
            Assertions.assertNotNull(pool);
            Assertions.assertTrue(pool.isClosed());
            Assertions.assertTrue(jedis.isConnected());

            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("wartosc", jedis.get("k1"));
        }
    }

    @Test
    void toJsonTest() throws Exception {
        try (Jedis jedis = pool.getResource()) {
            Assertions.assertNotNull(jedis);
            ClientAddress c1 = new Person(10, 1000.0d, "+48 7289", "PL", "Lodz", "street", "6A", "Jan", "Kowalski", "+48 65412");
            Account a1 = new Account(1, 1.2, AccountType.Normal, c1);
            Account a2 = new Account(2, 1.2, AccountType.Normal, c1);
            a1.deposit(1000);
            Transfer t1 = new Transfer(1, 2, 20, LocalDateTime.now());
            t1.setTransferId(1);

            String t1Json = jsonb.toJson(t1);
            jedis.set("" + t1.getTransferId(), t1Json);
            Transfer t2 = jsonb.fromJson(jedis.get("" + t1.getTransferId()), Transfer.class);

            Assertions.assertEquals(t1, t2);
            Assertions.assertEquals(a1, t2.getFromAccount());
            Assertions.assertEquals(a2, t2.getToAccount());

        } catch (Exception e) {
            throw new Exception();
        }
    }

    @Test
    void closeConnDuringTry() {
        try (Jedis jedis = pool.getResource()) {
            Assertions.assertNotNull(jedis);
            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("wartosc", jedis.get("k1"));

            jedis.close();
            Assertions.assertNotNull(pool);
            Assertions.assertFalse(pool.isClosed());
            Assertions.assertFalse(jedis.isBroken());

            Assertions.assertEquals("OK", jedis.set("k1", "wartosc"));
            Assertions.assertEquals("wartosc", jedis.get("k1"));
        }
    }
}
