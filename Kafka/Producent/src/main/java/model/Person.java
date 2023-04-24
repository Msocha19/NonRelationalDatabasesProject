package model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@BsonDiscriminator(key="_clientAdress", value = "person")
public class Person extends ClientAddress {

    @BsonProperty("name") @JsonbProperty("name")
    private String name;

    @BsonProperty("surname") @JsonbProperty("surname")
    private String surname;

    @BsonProperty("personalNumber") @JsonbProperty("personalNumber")
    private String personalNumber;

    @BsonCreator @JsonbCreator
    public Person(
            @BsonProperty("_id") @JsonbProperty("_id") int id,
            @BsonProperty("income") @JsonbProperty("income") double income,
            @BsonProperty("phoneNumber") @JsonbProperty("phoneNumber") String phoneNumber,
            @BsonProperty("country") @JsonbProperty("country") String country,
            @BsonProperty("city") @JsonbProperty("city") String city,
            @BsonProperty("street") @JsonbProperty("street") String street,
            @BsonProperty("number") @JsonbProperty("number") String number,
            @BsonProperty("name") @JsonbProperty("name") String name,
            @BsonProperty("surname") @JsonbProperty("surname") String surname,
            @BsonProperty("personalNumber") @JsonbProperty("personalNumber") String personalNumber,
            @BsonProperty("accounts") @JsonbProperty("accounts") List<Integer> accounts) {

        super(id, income, phoneNumber, country, city, street, number, accounts);
        this.name = name;
        this.surname = surname;
        this.personalNumber = personalNumber;
    }

    public Person(int id,
                  double income,
                  String phoneNumber,
                  String country,
                  String city,
                  String street,
                  String number,
                  String name,
                  String surname,
                  String personalNumber) {
        super(id, income, phoneNumber, country, city, street, number);
        this.name = name;
        this.surname = surname;
        this.personalNumber = personalNumber;
    }

    public String getName() {
        return name;
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
