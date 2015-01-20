package io.machinecode.chainlink.se;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Configure;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.schema.SubSystemSchema;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SeEnvironment implements Environment, AutoCloseable {

    private static final Logger log = Logger.getLogger(SeEnvironment.class);

    private final Map<String, JobOperatorImpl> operators = Collections.synchronizedMap(new THashMap<String, JobOperatorImpl>());
    private final String config;
    private boolean loaded = false;

    public SeEnvironment() {
        this(null);
    }

    public SeEnvironment(final String config) {
        this.config = config;
    }

    @Override
    public ExtendedJobOperator getSubsystemJobOperator(final String name) throws NoConfigurationWithIdException {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public JobOperatorImpl getJobOperator(final String name) throws NoConfigurationWithIdException {
        loadConfiguration();
        final JobOperatorImpl operator = operators.get(name);
        if (operator != null) {
            return operator;
        } else {
            throw new NoConfigurationWithIdException(Messages.format("CHAINLINK-031004.no.configuration.with.id", name));
        }
    }

    @Override
    public SubSystemSchema<?,?,?,?> getConfiguration() {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public SubSystemSchema<?,?,?,?> setConfiguration(final Configure configure) {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public void reload() {
        throw new IllegalStateException("Not implemented yet");
    }

    public Map<String, JobOperatorImpl> getJobOperators() {
        loadConfiguration();
        return Collections.unmodifiableMap(operators);
    }

    public synchronized void loadConfiguration() {
        if (!loaded) {
            _loadConfiguration();
            loaded = true;
        }
    }

    private void _loadConfiguration() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).findDeployment(Constants.DEFAULT);
        model.getJobOperator(Constants.DEFAULT);
        try {
            final String config = this.config != null
                    ? this.config
                    : System.getProperty(Constants.CHAINLINK_XML, Constants.Defaults.CHAINLINK_XML);
            final File configFile = new File(config);
            final InputStream stream;
            if (configFile.isFile()) {
                log.debugf("Found config file in filesystem at %s", config); //TODO Message
                stream = new FileInputStream(configFile);
            } else if (tccl.getResource(config) != null) {
                log.debugf("Found config file in classloader at %s", config); //TODO Message
                stream = tccl.getResourceAsStream(config);
            } else {
                log.debugf("No config found at %s", config); //TODO Message
                stream = null;
            }
            if (stream != null) {
                try {
                    model.loadChainlinkXml(stream);
                } finally {
                    stream.close();
                }
            }
            final SeConfigurationDefaults defaults = new SeConfigurationDefaults();
            for (final Map.Entry<String, JobOperatorModelImpl> entry : model.getJobOperators().entrySet()) {
                final JobOperatorModelImpl jobOperatorModel = entry.getValue();
                defaults.configureJobOperator(jobOperatorModel);
                operators.put(
                        entry.getKey(),
                        jobOperatorModel.createJobOperator()
                );
            }
        } catch (final RuntimeException e) {
            _close(e);
            throw e;
        } catch (final Throwable e) {
            _close(e);
            throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
    }

    @Override
    public void close() throws Exception {
        Throwable throwable = _close(null);
        if (throwable == null) {
            return;
        }
        if (throwable instanceof Exception) {
            throw (Exception)throwable;
        } else {
            throw new Exception(throwable);
        }
    }

    private Throwable _close(Throwable throwable) {
        for (final JobOperatorImpl op : operators.values()) {
            try {
                op.close();
            } catch(final Throwable t) {
                if (throwable != null) {
                    throwable.addSuppressed(t);
                } else {
                    throwable = t;
                }
            }
        }
        operators.clear();
        return throwable;
    }
}
