package repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import model.Transfer;
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
    protected ClassModel<Transfer> transferClassModel = ClassModel.builder(Transfer.class).enableDiscriminator(true).build();
    protected PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder()
            .register(transferClassModel).build();
    protected CodecRegistry  pojoCodecRegistry = CodecRegistries.fromProviders(pojoCodecProvider, PojoCodecProvider.builder().automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION, Conventions.CLASS_AND_PROPERTY_CONVENTION)).build());
    protected MongoClient mongoClient;
    protected MongoDatabase clientDB;
    protected String collectionName;
    protected Class<T> type;
    private MongoCollection<T> objCollection;

    protected void initDbConnection(String database) {
        MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
                .applyConnectionString(connectionString)
                .codecRegistry(fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry))
                .build();
        mongoClient = MongoClients.create(settings);
        clientDB = mongoClient.getDatabase(database);
    }

    public MongoRepository(String collectionName, Class<T> type, String database) {
        this.initDbConnection(database);
        this.collectionName = collectionName;
        this.type = type;
        objCollection = clientDB.getCollection(collectionName, type);
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
        clientDB.getCollection(collectionName).drop();
    }
}
