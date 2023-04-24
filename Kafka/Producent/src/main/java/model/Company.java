package model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.util.List;

@BsonDiscriminator(key="_clientAdress", value = "company")
public class Company extends ClientAddress {

    @BsonProperty("name") @JsonbProperty("name")
    private String name;

    @BsonProperty("NIP") @JsonbProperty("NIP")
    private String NIP;

    @BsonCreator @JsonbCreator
    public Company (
            @BsonProperty("_id") @JsonbProperty("_id") int id,
            @BsonProperty("income") @JsonbProperty("income") double income,
            @BsonProperty("phoneNumber") @JsonbProperty("phoneNumber") String phoneNumber,
            @BsonProperty("country") @JsonbProperty("country") String country,
            @BsonProperty("city") @JsonbProperty("city") String city,
            @BsonProperty("street") @JsonbProperty("street") String street,
            @BsonProperty("number") @JsonbProperty("number") String number,
            @BsonProperty("name") @JsonbProperty("name") String name,
            @BsonProperty("NIP") @JsonbProperty("NIP") String NIP,
            @BsonProperty("accounts") @JsonbProperty("accounts") List<Integer> accounts) {
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
