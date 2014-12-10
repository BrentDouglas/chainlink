package io.machinecode.chainlink.se;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.configuration.xml.XmlChainlink;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SeEnvironment implements Environment, AutoCloseable {

    private final Map<String, JobOperatorImpl> operators = Collections.synchronizedMap(new THashMap<String, JobOperatorImpl>());
    private final String config;
    private boolean loaded = false;

    public SeEnvironment() {
        this(Constants.CHAINLINK_XML);
    }

    public SeEnvironment(final String config) {
        this.config = config == null
                ? Constants.CHAINLINK_XML
                : config;
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
    public Map<String, JobOperatorImpl> getJobOperators() {
        loadConfiguration();
        return operators;
    }

    public synchronized void loadConfiguration() {
        if (!loaded) {
            _loadConfiguration();
            loaded = true;
        }
    }

    private void _loadConfiguration() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).getDeployment();
        model.getJobOperator(Constants.DEFAULT_CONFIGURATION);
        try {
            final File configFile = new File(config);
            if (configFile.isFile()) {
                final JAXBContext context = JAXBContext.newInstance(XmlChainlink.class);
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                final XmlChainlink xml = (XmlChainlink) unmarshaller.unmarshal(configFile);

                xml.configureDeployment(model, tccl);
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
            throw new RuntimeException(throwable);
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
