package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@Entity(defaultKeyspace = "bank")
@CqlName("clients")
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Person extends Client {

    @CqlName("name")
    private String name;

    @CqlName("surname")
    private String surname;

    @CqlName("personal_number")
    private String personalNumber;

    public Person(
            int id,
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
        super(accounts, city, country, id, income, number, phoneNumber, street, discriminator);
        this.name = name;
        this.surname = surname;
        this.personalNumber = personalNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getSurname() {
        return surname;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("surname", surname)
                .append("personalNumber", personalNumber)
                .append("Id", super.getId())
                .append("income", super.getIncome())
                .append("phoneNumber", super.getPhoneNumber())
                .append("accounts", super.getAccounts())
                .append("country", super.getCountry())
                .append("city", super.getCity())
                .append("street", super.getStreet())
                .append("number", super.getNumber())
                .toString();
    }
}
