package kafka;

import jakarta.json.bind.JsonbBuilder;
import model.Transfer;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import repository.MongoRepository;
import repository.Repository;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Consumer {

    private void receiveClients(KafkaConsumer consumer, Repository<Transfer> repository) {
        try {
            int messagesReceived = 0;
            Map<Integer, Long> offsets = new HashMap<>();
            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);
            while (true) {
                ConsumerRecords<Integer, String> records = consumer.poll(timeout);
                for (ConsumerRecord<Integer, String> record : records) {
                    String value = record.value();
                    StringBuilder builder = new StringBuilder(value);
                    int indexOfOpening = builder.indexOf("[");
                    int indexOfClosing = builder.indexOf("]");
                    String bankName = builder.substring(indexOfOpening + 1, indexOfClosing);
                    Transfer transfer = JsonbBuilder.create().fromJson(builder.replace(indexOfOpening, indexOfClosing + 1, "").toString(), Transfer.class);
                    repository.add(transfer);
                    System.out.println(bankName + " DATE: " + transfer.getTransferDate());
                    offsets.put(record.partition(), record.offset());
                    messagesReceived++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException we) {
            System.out.println("Job Finished");
        }
    }

    private KafkaConsumer<Integer, String> createConsumer() {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "consumers");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new KafkaConsumer<>(consumerConfig);
    }

    private List<KafkaConsumer<Integer, String>> createGroupOfCustomers() {
        List<KafkaConsumer<Integer, String>> consumerGroup = new ArrayList<>();
        KafkaConsumer<Integer, String> consumer1 = this.createConsumer();
        KafkaConsumer<Integer, String> consumer2 = this.createConsumer();
        consumer1.subscribe(List.of("transfers"));
        consumer2.subscribe(List.of("transfers"));
        consumerGroup.add(consumer1);
        consumerGroup.add(consumer2);
        return consumerGroup;
    }

    public void runConsumerGroup(String database) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<KafkaConsumer<Integer, String>> consumers = this.createGroupOfCustomers();
        for (int i = 0; i < consumers.size(); i++) {
            Repository<Transfer> repository = new MongoRepository<>("consumer", Transfer.class, database + (i+1));
            int finalI = i;
            executorService.execute(() -> receiveClients(consumers.get(finalI), repository));
        }
    }
}
