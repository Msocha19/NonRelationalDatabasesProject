package repository.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import model.constants.AccountIds;
import model.constants.ClientIds;
import model.constants.LoanIds;
import model.constants.TransferIds;

import java.net.InetSocketAddress;
import java.time.Duration;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public class CassandraClient {

    private CqlSession session;

    public CqlSession initSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", 9042))
                .addContactPoint(new InetSocketAddress("localhost", 9043))
                .withLocalDatacenter("dc1")
                .withConfigLoader(DriverConfigLoader.programmaticBuilder()
                        .withDuration(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, Duration.ofMillis(60000))
                        .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofMillis(60000))
                        .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofMillis(15000))
                        .build())
                .withAuthCredentials("cassandra", "cassandra")
                .build();
        if (session.getKeyspace().isEmpty()) {
            this.keySpace();
            session.close();
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress("localhost", 9042))
                    .addContactPoint(new InetSocketAddress("localhost", 9043))
                    .withLocalDatacenter("dc1")
                    .withKeyspace(CqlIdentifier.fromCql("bank"))
                    .withConfigLoader(DriverConfigLoader.programmaticBuilder()
                            .withDuration(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, Duration.ofMillis(60000))
                            .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofMillis(60000))
                            .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofMillis(15000))
                            .build())
                    .withAuthCredentials("cassandra", "cassandra")
                    .build();
        }
        this.createClients();
        this.createAccounts();
        this.createTransfer();
        this.createLoans();
        return this.session;
    }

    public void dropSession() {
        session.execute(
            SchemaBuilder.dropKeyspace(CqlIdentifier.fromCql("bank"))
                    .ifExists()
                    .build()
        );
    }

    public void createClients() {
        SimpleStatement createClients = SchemaBuilder.createTable(ClientIds.CLIENTS)
                .ifNotExists()
                .withPartitionKey(ClientIds.ID, DataTypes.INT)
                .withClusteringColumn(ClientIds.DISCRIMINATOR, DataTypes.TEXT)
                .withColumn(ClientIds.INCOME, DataTypes.DOUBLE)
                .withColumn(ClientIds.PHONE_NUMBER, DataTypes.TEXT)
                .withColumn(ClientIds.ACCOUNTS, DataTypes.listOf(DataTypes.INT))
                .withColumn(ClientIds.COUNTRY, DataTypes.TEXT)
                .withColumn(ClientIds.CITY, DataTypes.TEXT)
                .withColumn(ClientIds.STREET, DataTypes.TEXT)
                .withColumn(ClientIds.NUMBER, DataTypes.TEXT)
                .withColumn(ClientIds.NIP, DataTypes.TEXT)
                .withColumn(ClientIds.NAME, DataTypes.TEXT)
                .withColumn(ClientIds.SURNAME, DataTypes.TEXT)
                .withColumn(ClientIds.PERSONAL_NUMBER, DataTypes.TEXT)
                .withClusteringOrder(ClientIds.DISCRIMINATOR, ClusteringOrder.ASC)
                .build();
        session.execute(createClients);
    }

    public void createAccounts(){
        SimpleStatement createAccounts = SchemaBuilder.createTable(AccountIds.ACCOUNTS)
                .ifNotExists()
                .withPartitionKey(AccountIds.NUMBER, DataTypes.INT)
                .withClusteringColumn(AccountIds.TYPE, DataTypes.TEXT)
                .withColumn(AccountIds.BALANCE, DataTypes.DOUBLE)
                .withColumn(AccountIds.PERCENTAGE, DataTypes.DOUBLE)
                .withColumn(AccountIds.OWNER, DataTypes.INT)
                .withColumn(AccountIds.ACTIVE, DataTypes.BOOLEAN)
                .withClusteringOrder(AccountIds.TYPE, ClusteringOrder.ASC)
                .build();
        session.execute(createAccounts);
    }

    public void createTransfer() {
        SimpleStatement createTransfers = SchemaBuilder.createTable(TransferIds.TRANSFERS)
                .ifNotExists()
                .withPartitionKey(TransferIds.TRANSFER_ID, DataTypes.INT)
                .withClusteringColumn(TransferIds.TRANSFER_DATE, DataTypes.DATE)
                .withColumn(TransferIds.FROM_ACCOUNT, DataTypes.INT)
                .withColumn(TransferIds.TO_ACCOUNT, DataTypes.INT)
                .withColumn(TransferIds.AMOUNT, DataTypes.DOUBLE)
                .withClusteringOrder(TransferIds.TRANSFER_DATE, ClusteringOrder.ASC)
                .build();
        session.execute(createTransfers);
    }

    public void createLoans() {
        SimpleStatement createLoans = SchemaBuilder.createTable(LoanIds.LOANS)
                .ifNotExists()
                .withPartitionKey(LoanIds.LOAN_ID, DataTypes.INT)
                .withClusteringColumn(LoanIds.BEGIN_DATE, DataTypes.DATE)
                .withClusteringColumn(LoanIds.END_DATE, DataTypes.DATE)
                .withColumn(LoanIds.INIT_AMOUNT, DataTypes.DOUBLE)
                .withColumn(LoanIds.PERCENTAGE, DataTypes.DOUBLE)
                .withColumn(LoanIds.PAID_AMOUNT, DataTypes.DOUBLE)
                .withColumn(LoanIds.ACCOUNT, DataTypes.INT)//to change
                .withColumn(LoanIds.ARCHIVE, DataTypes.BOOLEAN)
                .withClusteringOrder(LoanIds.BEGIN_DATE, ClusteringOrder.ASC)
                .withClusteringOrder(LoanIds.END_DATE, ClusteringOrder.ASC)
                .build();
        session.execute(createLoans);
    }
    public void keySpace() {
        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql("bank"))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        session.execute(createKeyspace);
    }
}
