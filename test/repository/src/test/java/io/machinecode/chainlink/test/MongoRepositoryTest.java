package io.machinecode.chainlink.test;

import com.mongodb.MongoClient;
import io.machinecode.chainlink.repository.mongo.MongoExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.jboss.logging.Logger;
import org.jongo.Jongo;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MongoRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(MongoRepositoryTest.class);

    @Override
    protected ExecutionRepository _repository() throws Exception {
        final String host = System.getProperty("mongo.host", "127.0.0.1");
        final int port = Integer.parseInt(System.getProperty("mongo.port", "27017"));
        final String database = System.getProperty("mongo.database");
        log.infof("Connection: {\n\turl: '%s'\n}", host);
        final Jongo jongo = new Jongo(new MongoClient(host, port).getDB(database));
        return new MongoExecutionRepository(jongo, marshallerFactory().produce(null), true);
    }
}
