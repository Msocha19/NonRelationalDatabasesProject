package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Repository<T> {

    private List<T> collection = new ArrayList<>();

    public Repository() {};

    public T get(int id) throws Exception {
        if ( 0 < id && id > this.collection.size())
            throw new Exception("Nie znalezniono obiektu o podanym numerze");
        return this.collection.get(id);
    }

    public String report() {
        return this.collection.toString();
    }

    public int size() {
        return this.collection.size();
    }

    public void add(T obj) throws Exception {
        if (obj.equals(null)) {
            throw new Exception("Nie można dodać pustego obiektu do repozytorium");
        } else {
            this.collection.add(obj);
        }
    }

    public void remove(T obj) throws Exception {
        if (obj.equals(null)) {
            throw new Exception("Nie można usunąć pustego obiektu z repozytorium");
        } else  {
            collection.remove(obj);
        }
    }

    public List<T> findBy(Predicate<T> predicate) throws Exception {
        T tmp;
        List<T> found = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            tmp = this.get(i);
            if (tmp != null && predicate.test(tmp)) {
                found.add(tmp);
            }
        }
        return found;
    }
}
