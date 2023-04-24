package repository;

import java.util.List;

public interface Repository<T> {

    void add(T obj);
    void addMany(List<T> listObj);
    List<T> getAll();
    Object findNested(Object obj);
    T find(String field, Object value);
    List findAllNested(Object obj);
    void update(Object id, Object field, Object newValue, Object objId);
    List<T> findInherited(String descriptorKey, String descriptorValue);
    void delete(String id, Object objId);
    void deleteMany(String id, List<Integer> IDs);
    void wipeAll();
}
