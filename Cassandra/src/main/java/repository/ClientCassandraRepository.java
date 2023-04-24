package repository;

import com.datastax.oss.driver.api.core.CqlSession;
import model.Client;
import model.Company;
import model.Person;
import model.dao.ClientDao;
import model.mappers.ClientMapper;
import model.mappers.ClientMapperBuilder;

import java.util.List;

public class ClientCassandraRepository {
    private ClientDao clientDao;

    public ClientCassandraRepository(CqlSession session) {
        ClientMapper clientMapper = new ClientMapperBuilder(session).build();
        this.clientDao = clientMapper.clientDao();
    }

    public void createCompany(int id,
                              String country,
                              String name,
                              String NIP,
                              double income,
                              String phoneNumber,
                              List<Integer> accounts,
                              String city,
                              String street,
                              String number,
                              String discriminator) {
        Client client = new Company(id, country, name, NIP, income, phoneNumber, accounts, city, street, number, discriminator);
        clientDao.create(client);
    }

    public void createPerson(int id,
                             String country,
                             String name,
                             String surname,
                             String personalNumber,
                             double income,
                             String phoneNumber,
                             List<Integer> accounts,
                             String city,
                             String street,
                             String number,
                             String discriminator) {
        Client client = new Person(id, country, name, surname, personalNumber, income, phoneNumber, accounts, city, street, number, discriminator);
        clientDao.create(client);

    }

    public Client getClient(int id) {
        return clientDao.findById(id);
    }

    public Client deleteClient(int id) {
        Client client = this.clientDao.findById(id);
        this.clientDao.delete(client);
        return client;
    }

    public List<Client> getAllClients() {
        return this.clientDao.getAllClients();
    }

    public List<Client> getAllCompanies() {
        return this.clientDao.getAllCompanies();
    }

    public List<Client> getAllPeople() {
        return this.clientDao.getAllPeople();
    }

    public int findLastId() {
        return this.clientDao.findLastId();
    }

    public Boolean doesItContain(int id) {
        return this.clientDao.doesContain(id);
    }

    public void insertAccount(List<Integer> accounts, Client client) {
        this.clientDao.insertAccount(accounts, client);
    }

    public void updateClient(Client client) {
        this.clientDao.update(client);
    }
}
