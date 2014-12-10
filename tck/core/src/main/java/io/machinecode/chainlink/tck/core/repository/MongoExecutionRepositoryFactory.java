package io.machinecode.chainlink.tck.core.repository;

import com.mongodb.MongoClient;
import io.machinecode.chainlink.repository.mongo.MongoExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import org.jongo.Jongo;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
        final String host = System.getProperty("mongo.host");
        final int port = Integer.parseInt(System.getProperty("mongo.port"));
        final String database = System.getProperty("mongo.database");
        final Jongo jongo = new Jongo(new MongoClient(host, port).getDB(database));
        jongo.getDatabase().dropDatabase();
        return new MongoExecutionRepository(jongo, dependencies.getMarshalling(), true);
    }
}
