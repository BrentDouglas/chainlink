package io.machinecode.chainlink.mongo.test.repository;

import com.mongodb.MongoClient;
import io.machinecode.chainlink.repository.mongo.MongoRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.jboss.logging.Logger;
import org.jongo.Jongo;
import org.junit.After;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(MongoRepositoryTest.class);

    private Jongo jongo;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        final String host = System.getProperty("mongo.host", "127.0.0.1");
        final int port = Integer.parseInt(System.getProperty("mongo.port", "27017"));
        final String database = System.getProperty("mongo.database");
        log.infof("Connection: {\n\turl: '%s'\n}", host);
        jongo = new Jongo(new MongoClient(host, port).getDB(database));
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new MongoRepository(jongo, dependencies.getMarshalling(), true);
            }
        });
    }

    @After
    public void after() throws Exception {
        jongo.getCollection("job_instances").remove();
        jongo.getCollection("job_executions").remove();
        jongo.getCollection("step_executions").remove();
        jongo.getCollection("partition_executions").remove();
    }
}
