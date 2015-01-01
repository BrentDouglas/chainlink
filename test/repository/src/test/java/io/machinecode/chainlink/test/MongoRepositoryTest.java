package io.machinecode.chainlink.test;

import com.mongodb.MongoClient;
import io.machinecode.chainlink.repository.mongo.MongoExecutionRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.jboss.logging.Logger;
import org.jongo.Jongo;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(MongoRepositoryTest.class);

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        final String host = System.getProperty("mongo.host", "127.0.0.1");
        final int port = Integer.parseInt(System.getProperty("mongo.port", "27017"));
        final String database = System.getProperty("mongo.database");
        log.infof("Connection: {\n\turl: '%s'\n}", host);
        final Jongo jongo = new Jongo(new MongoClient(host, port).getDB(database));
        model.getExecutionRepository().setFactory(new ExecutionRepositoryFactory() {
            @Override
            public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new MongoExecutionRepository(jongo, dependencies.getMarshalling(), true);
            }
        });
    }
}
