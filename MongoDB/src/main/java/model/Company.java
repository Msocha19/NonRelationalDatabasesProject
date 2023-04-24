package model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.UUID;

@BsonDiscriminator(key="_clientAdress", value = "company")
public class Company extends ClientAddress {

    @BsonProperty("name")
    private String name;

    @BsonProperty("NIP")
    private String NIP;

    @BsonCreator
    public Company (
            @BsonProperty("_id") int id,
            @BsonProperty("income") double income,
            @BsonProperty("phoneNumber") String phoneNumber,
            @BsonProperty("country") String country,
            @BsonProperty("city") String city,
            @BsonProperty("street") String street,
            @BsonProperty("number") String number,
            @BsonProperty("name") String name,
            @BsonProperty("NIP") String NIP,
            @BsonProperty("accounts") List<Integer> accounts) {
        super(id, income, phoneNumber, country, city, street, number, accounts);
        this.name = name;
        this.NIP = NIP;
    }

    public Company(int id,
                   double income,
                   String phoneNumber,
                   String country,
                   String city,
                   String street,
                   String number,
                   String name,
                   String NIP) {
        super(id, income, phoneNumber, country, city, street, number);
        this.name = name;
        this.NIP = NIP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("NIP", NIP)
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
