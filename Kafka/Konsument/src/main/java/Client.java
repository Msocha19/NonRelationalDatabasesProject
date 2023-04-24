import kafka.Consumer;
import model.Transfer;
import repository.MongoRepository;
import repository.Repository;

public class Client {

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.runConsumerGroup(args[0]);
    }
}
