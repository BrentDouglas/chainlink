package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.util.Messages;
import org.glassfish.internal.data.ApplicationInfo;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishEnvironment implements Environment {

    private final ConcurrentMap<String, V> operators = new ConcurrentHashMap<String, V>();

    @Override
    public ExtendedJobOperator getJobOperator(final String id) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final Map.Entry<String, V> entry : operators.entrySet()) {
            final V v = entry.getValue();
            if (tccl.equals(v.info.getAppClassLoader())) {
                return v.ops.get(id);
            }
        }
        throw new NoConfigurationWithIdException("No configuration for id: " + id); //TODO Message
    }

    @Override
    public Map<String, ? extends ExtendedJobOperator> getJobOperators() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final Map.Entry<String, V> entry : operators.entrySet()) {
            final V v = entry.getValue();
            if (tccl.equals(v.info.getAppClassLoader())) {
                return v.ops;
            }
        }
        return Collections.emptyMap();
    }

    public void addApplication(final ApplicationInfo info) throws Exception {
        V v = this.operators.get(info.getName());
        if (v == null) {
            v = new V(info);
        }
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(Constants.CONFIGURATION_FACTORY_CLASS, ConfigurationFactory.class)
                    .resolve(info.getAppClassLoader());
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        try {
            final TransactionManager transactionManager = InitialContext.doLookup("java:comp/UserTransaction");
            for (final ConfigurationFactory factory : factories) {
                v.ops.put(
                        factory.getId(),
                        new JobOperatorImpl(factory.produce()
                                .setClassLoader(info.getAppClassLoader())
                                .setTransactionManager(transactionManager)
                                .build()
                        )
                );
            }
        } catch (final Exception e) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
    }

    public void removeApplication(final ApplicationInfo info) throws Exception {
        this.operators.remove(
                info.getName()
        );
    }

    private static class V {
        final ApplicationInfo info;
        final ConcurrentMap<String, JobOperatorImpl> ops;

        private V(final ApplicationInfo info) {
            this.info = info;
            this.ops = new ConcurrentHashMap<String, JobOperatorImpl>();
        }
    }
}
