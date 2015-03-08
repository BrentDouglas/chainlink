package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import io.machinecode.chainlink.core.transport.artifacts.TestTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.transport.Transport;
import org.junit.Before;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DistributedRepositoryTest extends RepositoryTest {

    private static final ConcurrentMap<String, TestTransport> transports = new ConcurrentHashMap<>();

    private Repository repo;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "first", Collections.<String>emptyList(), dependencies, properties);
                transports.put("first", transport);
                return transport;
            }
        });
    }

    @Before
    public void before() throws Exception {
        final JobOperatorImpl operator = new JobOperatorImpl(configuration());
        operator.open(configuration());
    }

    public Repository repository() throws Exception {
        if (repo == null) {
            final Transport transport = configuration().getTransport();
            final RepositoryId id = new UUIDId(transport);
            _registry.registerRepository(id, _repository);
            repo = transport.getRepository(id);
        }
        return repo;
    }
}
