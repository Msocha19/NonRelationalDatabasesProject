package model.dao;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import model.Client;
import model.Company;
import model.Person;
import model.providers.ClientQueryProvider;

import java.util.List;

@Dao
public interface ClientDao {
    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    Client findById(int id);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    void create(Client client);//Mozna zmienic zwracanie na Boolean

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    void delete(Client client);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    List<Client> getAllClients();


    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    List<Client> getAllCompanies();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    List<Client> getAllPeople();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    Integer findLastId();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    Boolean doesContain(int id);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    void insertAccount(List<Integer> accounts, Client client);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ClientQueryProvider.class, entityHelpers = {Company.class, Person.class})
    void update(Client client);
}
