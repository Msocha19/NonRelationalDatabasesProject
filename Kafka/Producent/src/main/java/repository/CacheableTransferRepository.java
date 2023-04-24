package repository;

import actions.Transfer;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import redis.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.GetExParams;
import java.util.ArrayList;
import java.util.List;


public class CacheableTransferRepository implements Repository<Transfer> {

    private JedisPool pool;
    private Jsonb jsonb;
    private long EXPIRE_TIME;
    private GetExParams params;
    private TransferRepository transferRepository;

    public CacheableTransferRepository() {
        this.pool = RedisConnection.create();
        jsonb = JsonbBuilder.create();
        transferRepository = new TransferRepository();
        EXPIRE_TIME = 6000L;
        params = new GetExParams().px(EXPIRE_TIME);
    }


    @Override
    public void add(Transfer obj) {
        try {
            try (Jedis jedis = pool.getResource()) {
                String key = Transfer.class.getName() + obj.getTransferId();
                String value = jsonb.toJson(obj);
                jedis.setex(key, EXPIRE_TIME, value);
                transferRepository.add(obj);
            }
        } catch (JedisException e) {
            transferRepository.add(obj);
        }
    }

    @Override
    public Transfer find(String field, Object value) {
        Transfer res;
        try {
            try (Jedis jedis = pool.getResource()) {
                String obj = jedis.getEx(Transfer.class.getName() + value, params);
                if (obj == null || "null".equals(obj))
                    throw new Exception("Key not found");
                res = jsonb.fromJson(obj, Transfer.class);
            }
        } catch (JedisException e) {
            res = transferRepository.find(field, value);
        } catch (Exception e) {
            res = transferRepository.find(field, value);
            this.add(res);
        }
        return res;
    }

    @Override
    public void delete(String id, Object objId) {
        transferRepository.delete(id, objId);
        try {
            try (Jedis jedis = pool.getResource()) {
                jedis.del(Transfer.class.getName() + objId);
            }
        } catch (JedisException ignored) {}
    }

    @Override
    public List findAllNested(Object obj) {
        return transferRepository.findAllNested(obj);
    }

    @Override   // keys(String) ma złożoność O(n) więc jest oki dla nas
    public List<Transfer> getAll() {
        try {
            try (Jedis jedis = pool.getResource()) {
                List<String> tmp = jedis.keys(Transfer.class.getName() + "*").stream().toList();
                List<Transfer> res = new ArrayList<>();
                for (String s: tmp) {
                    String t = jedis.getEx(s, params);
                    res.add(jsonb.fromJson(t, Transfer.class));
                }
                return res;
            }
        } catch (JedisException e) {
            return transferRepository.getAll();
        }
    }

    @Override
    public void wipeAll() {
        this.transferRepository.wipeAll();
        try (Jedis jedis = pool.getResource()) {
            jedis.flushDB();
        }
    }

    // ####################
    // UNUSED METHODS
    // ####################
    @Override
    public void deleteMany(String id, List<Integer> IDs) {
        ;;
    }

    @Override
    public void addMany(List<Transfer> listObj) {
        ;;
    }

    @Override
    public Object findNested(Object obj) {
        return null;
    }

    @Override
    public void update(Object id, Object field, Object newValue, Object objId) {
        ;;
    }

    @Override
    public List<Transfer> findInherited(String descriptorKey, String descriptorValue) {
        return null;
    }

}
