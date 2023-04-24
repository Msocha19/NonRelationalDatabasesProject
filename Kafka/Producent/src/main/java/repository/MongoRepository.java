package repository;

import actions.Loan;
import actions.Transfer;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import model.Account;
import model.ClientAddress;
import model.Company;
import model.Person;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRepository<T> implements Repository<T> {

    protected ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
    protected MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    protected ClassModel<ClientAddress> clientAddressClassModel = ClassModel.builder(ClientAddress.class).enableDiscriminator(true).build();
    protected ClassModel<Company> companyClassModel = ClassModel.builder(Company.class).enableDiscriminator(true).build();
    protected ClassModel<Person> personClassModel = ClassModel.builder(Person.class).enableDiscriminator(true).build();
    protected ClassModel<Account> accountClassModel = ClassModel.builder(Account.class).enableDiscriminator(true).build();
    protected ClassModel<Loan> loanClassModel = ClassModel.builder(Loan.class).enableDiscriminator(true).build();
    protected ClassModel<Transfer> transferClassModel = ClassModel.builder(Transfer.class).enableDiscriminator(true).build();
    protected PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder()
            .register(clientAddressClassModel, companyClassModel, personClassModel, accountClassModel, loanClassModel, transferClassModel).build();
    protected CodecRegistry  pojoCodecRegistry = CodecRegistries.fromProviders(pojoCodecProvider, PojoCodecProvider.builder().automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION, Conventions.CLASS_AND_PROPERTY_CONVENTION)).build());
    protected MongoClient mongoClient;
    protected MongoDatabase bankDB;
    protected String collectionName;
    protected Class<T> type;
    private MongoCollection<T> objCollection;

    protected void initDbConnection() {
        MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
                .applyConnectionString(connectionString)
                .codecRegistry(fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry))
                .build();
        mongoClient = MongoClients.create(settings);
        bankDB = mongoClient.getDatabase("bank");
    }

    public MongoRepository(String collectionName, Class<T> type) {
        this.initDbConnection();
        this.collectionName = collectionName;
        this.type = type;
        objCollection = bankDB.getCollection(collectionName, type);
    }


    public void add(T obj) {
        objCollection.insertOne(obj);
    }

    public void addMany(List<T> listObj) {
        objCollection.insertMany(listObj);
    }

    public List<T> getAll() {
        return objCollection.find().into(new ArrayList<>());
    }

    public List findAllNested(Object obj){
        return null;
    }

    public Object findNested(Object obj) {
        return null;
    }

    public T find(String field, Object value) {
        Bson filter = Filters.eq(field, value);
        return objCollection.find(filter).first();
    }

    public void update(Object id, Object field, Object newValue, Object objId) {
        Bson filter = Filters.eq((String) id, objId);
        Bson setUpdate = Updates.set((String) field, newValue);
        objCollection.updateOne(filter, setUpdate);
    }

    public List<T> findInherited(String descriptorKey, String descriptorValue) {
        Bson filter = Filters.eq(descriptorKey, descriptorValue);
        return objCollection.find(filter).into(new ArrayList<>());
    }

    public void delete(String id, Object objId) {
        Bson filter = Filters.eq(id, objId);
        objCollection.findOneAndDelete(filter);
    }

    public void deleteMany(String id, List<Integer> IDs) {
        Bson filter = Filters.all(id, IDs);
        objCollection.deleteMany(filter);
    }

    public void wipeAll() {
        bankDB.getCollection(collectionName).drop();
    }
}
