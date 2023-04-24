package model.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import model.Account;
import model.Client;
import model.Company;
import model.Person;
import model.constants.ClientIds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class ClientQueryProvider {
    private CqlSession session;
    private EntityHelper<Company> companyEntityHelper;
    private EntityHelper<Person> personEntityHelper;

    public ClientQueryProvider(MapperContext ctx, EntityHelper<Company> companyEntityHelper, EntityHelper<Person> personEntityHelper) {
        this.session = ctx.getSession();
        this.companyEntityHelper = companyEntityHelper;
        this.personEntityHelper = personEntityHelper;
    }

    public void create(Client client) {
        session.execute(
                switch (client.getDiscriminator()) {
                    case "company" -> {
                        Company company = (Company) client;
                        yield session.prepare(companyEntityHelper.insert().build())
                                .bind()
                                .setInt(ClientIds.ID, company.getId())
                                .setDouble(ClientIds.INCOME, company.getIncome())
                                .setString(ClientIds.PHONE_NUMBER, company.getPhoneNumber())
                                .setString(ClientIds.COUNTRY, company.getCountry())
                                .setString(ClientIds.CITY, company.getCity())
                                .setString(ClientIds.STREET, company.getStreet())
                                .setString(ClientIds.NUMBER, company.getNumber())
                                .setString(ClientIds.NAME, company.getName())
                                .setString(ClientIds.NIP, company.getNIP())
                                .setString(ClientIds.DISCRIMINATOR, company.getDiscriminator())
                                .setList(ClientIds.ACCOUNTS, company.getAccounts(), Integer.class);
                    }

                    case "person" -> {
                        Person person = (Person) client;
                        yield session.prepare(personEntityHelper.insert().build())
                                .bind()
                                .setInt(ClientIds.ID, person.getId())
                                .setDouble(ClientIds.INCOME, person.getIncome())
                                .setString(ClientIds.PHONE_NUMBER, person.getPhoneNumber())
                                .setString(ClientIds.COUNTRY, person.getCountry())
                                .setString(ClientIds.CITY, person.getCity())
                                .setString(ClientIds.STREET, person.getStreet())
                                .setString(ClientIds.NUMBER, person.getNumber())
                                .setString(ClientIds.NAME, person.getName())
                                .setString(ClientIds.SURNAME, person.getSurname())
                                .setString(ClientIds.PERSONAL_NUMBER, person.getPersonalNumber())
                                .setString(ClientIds.DISCRIMINATOR, person.getDiscriminator())
                                .setList(ClientIds.ACCOUNTS, person.getAccounts(), Integer.class);
                    }
                    default -> throw new IllegalArgumentException();
                });
    }

    public void delete(Client client) {
        session.execute(
            switch (client.getDiscriminator()) {
                case "company" -> {
                    Company company = (Company) client;
                    yield session.prepare(companyEntityHelper.deleteByPrimaryKey().build())
                            .bind()
                            .setInt(ClientIds.ID, company.getId())
                            .setString(ClientIds.DISCRIMINATOR, company.getDiscriminator());
                }
                case "person" -> {
                    Person person = (Person) client;
                    yield session.prepare(personEntityHelper.deleteByPrimaryKey().build())
                            .bind()
                            .setInt(ClientIds.ID, person.getId())
                            .setString(ClientIds.DISCRIMINATOR, person.getDiscriminator());
                }
                default -> throw new IllegalArgumentException();
            });
    }

    public Client findById(int id) {
        Select selectClient = QueryBuilder
                .selectFrom(ClientIds.CLIENTS)
                .all()
                .where(Relation.column(ClientIds.ID).isEqualTo(literal(id)));
        Row row = session.execute(selectClient.build()).one();
        String discriminator = row.getString(ClientIds.DISCRIMINATOR);
        return switch (discriminator) {
            case "company" -> getCompany(row);
            case "person" -> getPerson(row);
            default -> throw new IllegalArgumentException();
        };
    }

    public List<Client> getAllClients() {
        Select all = QueryBuilder
                .selectFrom(ClientIds.CLIENTS)
                .all();
        List<Row> rows = session.execute(all.build()).all();
        List<Client> clients = new ArrayList<>();
        for (Row row : rows) {
            String discriminator = row.getString(ClientIds.DISCRIMINATOR);
            if (discriminator.equals("company")) {
                clients.add(this.getCompany(row));
            } else {
                clients.add(this.getPerson(row));
            }
        }
        return clients;
    }

    public List<Client> getAllCompanies() {
        Select all = QueryBuilder
                .selectFrom(ClientIds.CLIENTS)
                .all()
                .allowFiltering()
                .where(Relation.column(ClientIds.DISCRIMINATOR).isEqualTo(literal("company")));
        List<Row> rows = session.execute(all.build()).all();
        List<Client> clients = new ArrayList<>();
        for (Row row : rows) {
            clients.add(this.getCompany(row));
        }
        return clients;
    }

    public List<Client> getAllPeople() {
        Select all = QueryBuilder
                .selectFrom(ClientIds.CLIENTS)
                .all()
                .allowFiltering()
                .where(Relation.column(ClientIds.DISCRIMINATOR).isEqualTo(literal("person")));
        List<Row> rows = session.execute(all.build()).all();
        List<Client> clients = new ArrayList<>();
        for (Row row : rows) {
            clients.add(this.getPerson(row));
        }
        return clients;
    }

    public int findLastId() {
        Select last = QueryBuilder.selectFrom(ClientIds.CLIENTS)
                .all();
        List<Row> rows = session.execute(last.build()).all();
        List<Integer> all = new ArrayList<>();
        if (rows.size() > 0) {
            for (Row row : rows) {
                all.add(row.getInt(ClientIds.ID));
            }
            return Collections.max(all);
        }
        return 0;
    }

    public Boolean doesContain(int id) {
        Select doesItContain = QueryBuilder
                .selectFrom(ClientIds.CLIENTS)
                .all()
                .where(Relation.column(ClientIds.ID).isEqualTo(literal(id)));
        Row row = session.execute(doesItContain.build()).one();
        return row != null;
    }

    public void insertAccount(List<Integer> accounts, Client client) {
        session.execute(
                switch (client.getDiscriminator()) {
                    case "company" -> {
                        Company company = (Company) client;
                        yield session.prepare(companyEntityHelper.updateByPrimaryKey().build())
                                .bind()
                                .setInt(ClientIds.ID, company.getId())
                                .setString(ClientIds.DISCRIMINATOR, company.getDiscriminator())
                                .setList(ClientIds.ACCOUNTS, company.getAccounts(), Integer.class);
                    }
                    case "person" -> {
                        Person person = (Person) client;
                        yield session.prepare(personEntityHelper.updateByPrimaryKey().build())
                                .bind()
                                .setInt(ClientIds.ID, person.getId())
                                .setString(ClientIds.DISCRIMINATOR, person.getDiscriminator())
                                .setList(ClientIds.ACCOUNTS, person.getAccounts(), Integer.class);
                    }
                    default -> throw new IllegalArgumentException();
                });
    }

    public void update(Client client) {
        session.execute(
                switch (client.getDiscriminator()) {
                    case "company" -> {
                        Company company = (Company) client;
                        yield session.prepare(companyEntityHelper.updateByPrimaryKey().build())
                                .bind()
                                .setInt(ClientIds.ID, company.getId())
                                .setDouble(ClientIds.INCOME, company.getIncome())
                                .setString(ClientIds.PHONE_NUMBER, company.getPhoneNumber())
                                .setString(ClientIds.COUNTRY, company.getCountry())
                                .setString(ClientIds.CITY, company.getCity())
                                .setString(ClientIds.STREET, company.getStreet())
                                .setString(ClientIds.NUMBER, company.getNumber())
                                .setString(ClientIds.NAME, company.getName())
                                .setString(ClientIds.NIP, company.getNIP())
                                .setString(ClientIds.DISCRIMINATOR, company.getDiscriminator())
                                .setList(ClientIds.ACCOUNTS, company.getAccounts(), Integer.class);

                    }
                    case "person" -> {
                        Person person = (Person) client;
                        yield session.prepare(personEntityHelper.updateByPrimaryKey().build())
                                .bind()
                                .setInt(ClientIds.ID, person.getId())
                                .setDouble(ClientIds.INCOME, person.getIncome())
                                .setString(ClientIds.PHONE_NUMBER, person.getPhoneNumber())
                                .setString(ClientIds.COUNTRY, person.getCountry())
                                .setString(ClientIds.CITY, person.getCity())
                                .setString(ClientIds.STREET, person.getStreet())
                                .setString(ClientIds.NUMBER, person.getNumber())
                                .setString(ClientIds.NAME, person.getName())
                                .setString(ClientIds.SURNAME, person.getSurname())
                                .setString(ClientIds.PERSONAL_NUMBER, person.getPersonalNumber())
                                .setString(ClientIds.DISCRIMINATOR, person.getDiscriminator())
                                .setList(ClientIds.ACCOUNTS, person.getAccounts(), Integer.class);
                    }
                    default -> throw new IllegalArgumentException();
                });
    }

    private Company getCompany(Row company) {
        return new Company(
                company.getInt(ClientIds.ID),
                company.getString(ClientIds.COUNTRY),
                company.getString(ClientIds.NAME),
                company.getString(ClientIds.NIP),
                company.getDouble(ClientIds.INCOME),
                company.getString(ClientIds.PHONE_NUMBER),
                company.getList(ClientIds.ACCOUNTS, Integer.class),
                company.getString(ClientIds.CITY),
                company.getString(ClientIds.STREET),
                company.getString(ClientIds.NUMBER),
                company.getString(ClientIds.DISCRIMINATOR)
        );
    }

    private Person getPerson(Row person) {
        return new Person(
                person.getInt(ClientIds.ID),
                person.getString(ClientIds.COUNTRY),
                person.getString(ClientIds.NAME),
                person.getString(ClientIds.SURNAME),
                person.getString(ClientIds.PERSONAL_NUMBER),
                person.getDouble(ClientIds.INCOME),
                person.getString(ClientIds.PHONE_NUMBER),
                person.getList(ClientIds.ACCOUNTS, Integer.class),
                person.getString(ClientIds.CITY),
                person.getString(ClientIds.STREET),
                person.getString(ClientIds.NUMBER),
                person.getString(ClientIds.DISCRIMINATOR)
        );
    }
}
