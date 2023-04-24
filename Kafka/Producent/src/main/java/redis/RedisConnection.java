package redis;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.Properties;

public class RedisConnection {

    private static final Properties configFile = new Properties();
    private static JedisPool pool;

    public static JedisPool create() {
        JedisClientConfig config = DefaultJedisClientConfig.builder().socketTimeoutMillis(2000).build();
        String hostname;
        int port;

        try {
            configFile.load(RedisConnection.class.getResourceAsStream("redis.conf"));
            hostname = configFile.getProperty("hostname");
            port = Integer.parseInt(configFile.getProperty("port"));
        }  catch (IOException | NullPointerException e) {
            hostname = "localhost";
            port = 6379;
        }

        pool = new JedisPool(new HostAndPort(hostname, port), config);
        return pool;
    }
}
