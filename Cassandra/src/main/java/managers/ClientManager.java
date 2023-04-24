package managers;

import com.datastax.oss.driver.api.core.CqlSession;
import io.netty.util.collection.CharObjectMap;
import model.Account;
import model.Client;
import model.Company;
import model.Person;
import repository.AccountCassandraRepository;
import repository.ClientCassandraRepository;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {
    private ClientCassandraRepository clientCassandraRepository;
    private AccountCassandraRepository accountCassandraRepository;

    public ClientManager(CqlSession session) {
        this.clientCassandraRepository = new ClientCassandraRepository(session);
        this.accountCassandraRepository = new AccountCassandraRepository(session);
    }

    public void createCompany(String country, String name, String NIP, double income, String phoneNumber, String city, String street, String number) {
        int ID = clientCassandraRepository.findLastId() + 1;
        this.clientCassandraRepository.createCompany(ID, country, name, NIP, income, phoneNumber, new ArrayList<>(), city, street, number, "company");
    }

    public void createCompany(Company company) {
        this.clientCassandraRepository.createCompany(company.getId(), company.getCountry(), company.getName(), company.getNIP(), company.getIncome(), company.getPhoneNumber(), company.getAccounts(), company.getCity(), company.getStreet(), company.getNumber(), company.getDiscriminator());
    }

    public void createPerson(String country, String name, String surname, String personalNumber, double income, String phoneNumber, String city, String street, String number) {
        int ID = clientCassandraRepository.findLastId() + 1;
        this.clientCassandraRepository.createPerson(ID, country, name, surname, personalNumber, income, phoneNumber, new ArrayList<>(), city, street, number, "person");
    }

    public void createPerson(Person person) {
        this.clientCassandraRepository.createPerson(person.getId(), person.getCountry(), person.getName(), person.getSurname(), person.getPersonalNumber(), person.getIncome(), person.getPhoneNumber(), person.getAccounts(), person.getCity(), person.getStreet(), person.getPersonalNumber(), person.getDiscriminator());
    }

    public Client getClientById(int id) {
        return this.clientCassandraRepository.getClient(id);
    }

    public List<Integer> getClientAccountsIds(int id) throws Exception{
        return this.getClientById(id).getAccounts();
    }

    public List<Account> getClientAccounts(int id) throws Exception {
        return this.accountCassandraRepository.findAllAccountsForClient(id);
    }

    public List<Client> getAll() {
        return this.clientCassandraRepository.getAllClients();
    }

    public List<Client> getCompanies() {
        return this.clientCassandraRepository.getAllCompanies();
    }

    public List<Client> getPeople() {
        return this.clientCassandraRepository.getAllPeople();
    }

    public void deleteClient(int id) {
        Client deleted = this.clientCassandraRepository.deleteClient(id);
        for (Integer i : deleted.getAccounts()) {
            this.accountCassandraRepository.deleteAccount(i);
        }
    }

    public void updateCompany(int clientId,double income, String phoneNumber, String country, String city, String street, String number, String name, String NIP) {
        Client client = this.clientCassandraRepository.getClient(clientId);
        if (client.getClass().equals(Company.class)) {
            Company company = (Company) client;
            if (income > 0) {
                client.setIncome(income);
            }
            if (phoneNumber != null && !phoneNumber.equals("")) {
                company.setPhoneNumber(phoneNumber);
            }
            if (country != null && !country.equals("")) {
                company.setCountry(country);
            }
            if (city != null && !city.equals("")) {
                company.setCity(city);
            }
            if (street != null && !street.equals("")) {
                company.setStreet(street);
            }
            if (number != null && !number.equals("")) {
                company.setNumber(number);
            }
            if (name != null && !name.equals("")) {
                company.setName(name);
            }
            if (NIP != null && !NIP.equals("")) {
                company.setNIP(NIP);
            }
            this.clientCassandraRepository.updateClient(company);
        }
    }

    public void updatePerson(int clientId,double income, String phoneNumber, String country, String city, String street, String number, String name, String surname, String personalNumber) {
        Client client = this.clientCassandraRepository.getClient(clientId);
        if (client.getClass().equals(Person.class)) {
            Person person = (Person) client;
            if (income > 0) {
                person.setIncome(income);
            }
            if (phoneNumber != null && !phoneNumber.equals("")) {
                person.setPhoneNumber(phoneNumber);
            }
            if (country != null && !country.equals("")) {
                person.setCountry(country);
            }
            if (city != null && !city.equals("")) {
                person.setCity(city);
            }
            if (street != null && !street.equals("")) {
                person.setStreet(street);
            }
            if (number != null && !number.equals("")) {
                person.setNumber(number);
            }
            if (name != null && !name.equals("")) {
                person.setName(name);
            }
            if (surname != null && !surname.equals("")) {
                person.setSurname(surname);
            }
            if (personalNumber != null && !personalNumber.equals("")) {
                person.setPersonalNumber(personalNumber);
            }
            this.clientCassandraRepository.updateClient(person);
        }
    }

    public void update(Client client) {
        this.clientCassandraRepository.updateClient(client);
    }
}
