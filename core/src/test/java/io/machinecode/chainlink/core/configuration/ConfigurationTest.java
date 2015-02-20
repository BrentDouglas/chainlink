package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryRepositoryFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.batch.api.Batchlet;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationTest extends Assert {

    public void defaults(final ClassLoader tccl, final JobOperatorModel model) throws Exception {
        model.getClassLoader().setDefaultFactory(new ClassLoaderFactoryImpl(tccl));
        model.getTransactionManager().setDefaultFactory(new LocalTransactionManagerFactory());
        model.getRepository().setDefaultFactory(new MemoryRepositoryFactory());
        model.getMarshalling().setDefaultFactory(new JdkMarshallingFactory());
        model.getTransport().setDefaultFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultFactory(new EventedExecutorFactory());
    }

    private JobOperatorModelImpl op() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final DeploymentModelImpl deployment = model.getDeployment(Constants.DEFAULT);
        deployment.loadChainlinkXml(tccl.getResourceAsStream("test-chainlink.xml"));
        final JobOperatorModelImpl op = deployment.getJobOperator("default");
        defaults(tccl, op);
        return op;
    }

    @Test
    public void basicConfigTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean firstInjected = new AtomicBoolean();
        final AtomicBoolean secondInjected = new AtomicBoolean();
        op.getArtifactLoader("first-artifact-loader").setValue(new ArtifactLoader() {
            @Override
            public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                firstInjected.set(true);
                return null;
            }
        });
        op.getArtifactLoader("second-artifact-loader").setFactory(new ArtifactLoaderFactory() {
            @Override
            public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return new ArtifactLoader() {
                    @Override
                    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                        secondInjected.set(true);
                        return null;
                    }
                };
            }
        });
        final ConfigurationImpl conf = op.getConfiguration();
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        // Check an artifact can be loaded by it's factory fqcn
        // Also check an artifact can be loaded by an arbitrary ref if the loader supports it
        final Batchlet testBatchlet = conf.getArtifactLoader().load("testBatchlet", Batchlet.class, tccl);
        assertNotNull(testBatchlet);
        assertTrue(testBatchlet instanceof TestBatchlet);

        // Check an artifact can be loaded by the artifact fqcn
        final Batchlet otherBatchlet = conf.getArtifactLoader().load("otherBatchlet", Batchlet.class, tccl);
        assertNotNull(otherBatchlet);
        assertTrue(otherBatchlet instanceof TestBatchlet);

        conf.getArtifactLoader().load("not-a-thing", Batchlet.class, Thread.currentThread().getContextClassLoader());
        // Check setting by value works
        assertTrue(firstInjected.get());

        // Check setting by factory works
        assertTrue(secondInjected.get());
    }

    @Test
    public void valueOverValueClassTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean valueInjected = new AtomicBoolean();
        op.getArtifactLoader("first-artifact-loader").setValue(new ArtifactLoader() {
            @Override
            public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                valueInjected.set(true);
                return null;
            }
        });
        op.getArtifactLoader("first-artifact-loader").setValueClass(ClassArtifactLoader.class);
        final ConfigurationImpl conf = op.getConfiguration();

        conf.getArtifactLoader().load("not-a-thing", Batchlet.class, Thread.currentThread().getContextClassLoader());
        // Check value overrides factory
        assertTrue(valueInjected.get());
        assertFalse(ClassArtifactLoader.injected.get());
    }

    @Test
    public void valueClassOverFactoryTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean factoryInjected = new AtomicBoolean();
        op.getArtifactLoader("first-artifact-loader").setValueClass(ClassArtifactLoader.class);
        op.getArtifactLoader("first-artifact-loader").setFactory(new ArtifactLoaderFactory() {
            @Override
            public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return new ArtifactLoader() {
                    @Override
                    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                        factoryInjected.set(true);
                        return null;
                    }
                };
            }
        });
        final ConfigurationImpl conf = op.getConfiguration();

        conf.getArtifactLoader().load("not-a-thing", Batchlet.class, Thread.currentThread().getContextClassLoader());
        // Check value overrides factory
        assertTrue(ClassArtifactLoader.injected.get());
        assertFalse(factoryInjected.get());
    }

    @Test
    public void factoryOverFactoryClassTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean factoryInjected = new AtomicBoolean();
        op.getArtifactLoader("first-artifact-loader").setFactory(new ArtifactLoaderFactory() {
            @Override
            public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return new ArtifactLoader() {
                    @Override
                    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                        factoryInjected.set(true);
                        return null;
                    }
                };
            }
        });
        op.getArtifactLoader("first-artifact-loader").setFactoryClass(ClassArtifactLoaderFactory.class);

        final ConfigurationImpl conf = op.getConfiguration();

        conf.getArtifactLoader().load("not-a-thing", Batchlet.class, Thread.currentThread().getContextClassLoader());
        // Check factory overrides factory class
        assertTrue(factoryInjected.get());
        assertFalse(ClassArtifactLoaderFactory.injected.get());
    }

    @Test
    public void factoryClassOverRefTest() throws Exception {
        final JobOperatorModelImpl op = op();
        op.getArtifactLoader("first-artifact-loader").setFactoryClass(ClassArtifactLoaderFactory.class);
        op.getArtifactLoader("first-artifact-loader").setRef(RefArtifactLoaderFactory.class.getName());

        final ConfigurationImpl conf = op.getConfiguration();

        conf.getArtifactLoader().load("not-a-thing", Batchlet.class, Thread.currentThread().getContextClassLoader());
        // Check factory overrides factory class
        assertTrue(ClassArtifactLoaderFactory.injected.get());
        assertFalse(RefArtifactLoaderFactory.injected.get());
    }

    @Before
    public void before() throws Exception {
        ClassArtifactLoader.injected.set(false);
        ClassArtifactLoaderFactory.injected.set(false);
        RefArtifactLoaderFactory.injected.set(false);
    }

    public static class ClassArtifactLoader implements ArtifactLoader {

        static final AtomicBoolean injected = new AtomicBoolean();

        @Override
        public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
            injected.set(true);
            return null;
        }
    }

    public static class ClassArtifactLoaderFactory implements ArtifactLoaderFactory {

        static final AtomicBoolean injected = new AtomicBoolean();

        @Override
        public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
            return new ArtifactLoader() {
                @Override
                public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                    injected.set(true);
                    return null;
                }
            };
        }
    }

    public static class RefArtifactLoaderFactory implements ArtifactLoaderFactory {

        static final AtomicBoolean injected = new AtomicBoolean();

        @Override
        public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
            return new ArtifactLoader() {
                @Override
                public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
                    injected.set(true);
                    return null;
                }
            };
        }
    }
}
