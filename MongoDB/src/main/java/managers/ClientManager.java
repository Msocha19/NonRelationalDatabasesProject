package managers;

import model.ClientAddress;
import model.Company;
import model.Person;
import repository.MongoRepository;

import java.util.List;

public class ClientManager {
    private MongoRepository<ClientAddress> repository;

    public ClientManager() {
        this.repository = new MongoRepository<>("client", ClientAddress.class);
    }

    public void createCompany(int id, double income, String phoneNumber, String country, String city, String street, String number, String name, String NIP) throws Exception {
        ClientAddress clientAddress = new Company(id, income, phoneNumber, country, city, street, number, name, NIP);
        if (this.contains(clientAddress)) {
            throw new Exception("Client with this id already exists!");
        } else {
            this.repository.add(clientAddress);
        }
    }

    public List<ClientAddress> findCompany() {
        return this.repository.findInherited("_clientAdress", "company");
    }

    public List<ClientAddress> findPerson() {
        return this.repository.findInherited("_clientAdress", "person");
    }

    public void createPerson(int id, double income, String phoneNumber, String country, String city, String street, String number, String name, String surname, String personalNumber) throws Exception {
        ClientAddress clientAddress = new Person(id, income, phoneNumber, country, city, street, number, name, surname, personalNumber);
        if (this.contains(clientAddress)) {
            throw new Exception("Client with this id already exists!");
        } else {
            this.repository.add(clientAddress);
        }
    }

    public void create(ClientAddress clientAddress) throws Exception {
        if (this.contains(clientAddress)) {
            throw new Exception("Client with this id already exists!");
        } else {
            this.repository.add(clientAddress);
        }
    }

    public List<Integer> getOpenedAccountsID(Integer id) {
        ClientAddress ca = this.find(id);
        return ca.getAccounts();
    }

    public void updateAccountsID(Integer id, Integer oldValue, Integer newValue) {
        List<Integer> list = this.getOpenedAccountsID(id);
        int i = list.indexOf(oldValue);
        list.remove(i);
        list.add(i, newValue);
        this.repository.update("_id", "accounts", list, id);
    }

    public void deleteClient(Integer id) throws Exception {
        ClientAddress clientAddress = repository.find("_id", id);
        AccountManager accountManager = new AccountManager();
        if (this.contains(clientAddress)) {
            accountManager.deleteMany(clientAddress.getAccounts());
            repository.delete("_id", clientAddress.getId());
        } else {
            throw new Exception("This client does not exist!");
        }
    }

    public void update(Object id, Object field, Object newValue) throws Exception {
        AccountManager accountManager = new AccountManager();
        if (this.contains(this.repository.find("_id", id))) {
            repository.update("_id", field, newValue, id);
            for(Integer i : repository.find("_id", id).getAccounts()) {
                accountManager.update(i, "client", this.repository.find("_id", id));
            }
        } else {
            throw new Exception("This client does not exist!");
        }
    }
    public boolean contains(ClientAddress clientAddress) {
        if (repository.getAll().contains(clientAddress)) {
            return true;
        }
        return false;
    }

    public void deleteAll() {
        AccountManager accountManager = new AccountManager();
        accountManager.deleteAll();
        this.repository.wipeAll();
    }

    public ClientAddress find(Integer id) {
        return this.repository.find("_id", id);
    }

    public List<ClientAddress> findAll() {
        return this.repository.getAll();
    }
}
