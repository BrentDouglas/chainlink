package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.inject.Injector;
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
        model.getExecutionRepository().setDefaultFactory(new MemoryExecutionRepositoryFactory());
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
        op.getInjector("first-injector").setValue(new Injector() {
            @Override
            public boolean inject(final Object bean) throws Exception {
                firstInjected.set(true);
                return false;
            }
        });
        op.getInjector("second-injector").setFactory(new InjectorFactory() {
            @Override
            public Injector produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return new Injector() {
                    @Override
                    public boolean inject(final Object bean) throws Exception {
                        secondInjected.set(true);
                        return false;
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

        conf.getInjector().inject(new Object());

        // Check setting by value works
        assertTrue(firstInjected.get());

        // Check setting by factory works
        assertTrue(secondInjected.get());
    }

    @Test
    public void valueOverValueClassTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean valueInjected = new AtomicBoolean();
        op.getInjector("first-injector").setValue(new Injector() {
            @Override
            public boolean inject(final Object bean) throws Exception {
                valueInjected.set(true);
                return false;
            }
        });
        op.getInjector("first-injector").setValueClass(ClassInjector.class);
        final ConfigurationImpl conf = op.getConfiguration();

        conf.getInjector().inject(new Object());

        // Check value overrides factory
        assertTrue(valueInjected.get());
        assertFalse(ClassInjector.injected.get());
    }

    @Test
    public void valueClassOverFactoryTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean factoryInjected = new AtomicBoolean();
        op.getInjector("first-injector").setValueClass(ClassInjector.class);
        op.getInjector("first-injector").setFactory(new InjectorFactory() {
            @Override
            public Injector produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return new Injector() {
                    @Override
                    public boolean inject(final Object bean) throws Exception {
                        factoryInjected.set(true);
                        return false;
                    }
                };
            }
        });
        final ConfigurationImpl conf = op.getConfiguration();

        conf.getInjector().inject(new Object());

        // Check value overrides factory
        assertTrue(ClassInjector.injected.get());
        assertFalse(factoryInjected.get());
    }

    @Test
    public void factoryOverFactoryClassTest() throws Exception {
        final JobOperatorModelImpl op = op();

        final AtomicBoolean factoryInjected = new AtomicBoolean();
        op.getInjector("first-injector").setFactory(new InjectorFactory() {
            @Override
            public Injector produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return new Injector() {
                    @Override
                    public boolean inject(final Object bean) throws Exception {
                        factoryInjected.set(true);
                        return false;
                    }
                };
            }
        });
        op.getInjector("first-injector").setFactoryClass(ClassInjectorFactory.class);

        final ConfigurationImpl conf = op.getConfiguration();

        conf.getInjector().inject(new Object());

        // Check factory overrides factory class
        assertTrue(factoryInjected.get());
        assertFalse(ClassInjectorFactory.injected.get());
    }

    @Test
    public void factoryClassOverRefTest() throws Exception {
        final JobOperatorModelImpl op = op();
        op.getInjector("first-injector").setFactoryClass(ClassInjectorFactory.class);
        op.getInjector("first-injector").setRef(RefInjectorFactory.class.getName());

        final ConfigurationImpl conf = op.getConfiguration();

        conf.getInjector().inject(new Object());

        // Check factory overrides factory class
        assertTrue(ClassInjectorFactory.injected.get());
        assertFalse(RefInjectorFactory.injected.get());
    }

    @Before
    public void before() throws Exception {
        ClassInjector.injected.set(false);
        ClassInjectorFactory.injected.set(false);
        RefInjectorFactory.injected.set(false);
    }

    public static class ClassInjector implements Injector {

        static final AtomicBoolean injected = new AtomicBoolean();

        @Override
        public boolean inject(final Object bean) throws Exception {
            injected.set(true);
            return false;
        }
    }

    public static class ClassInjectorFactory implements InjectorFactory {

        static final AtomicBoolean injected = new AtomicBoolean();

        @Override
        public Injector produce(final Dependencies dependencies, final Properties properties) throws Exception {
            return new Injector() {
                @Override
                public boolean inject(final Object bean) throws Exception {
                    injected.set(true);
                    return false;
                }
            };
        }
    }

    public static class RefInjectorFactory implements InjectorFactory {

        static final AtomicBoolean injected = new AtomicBoolean();

        @Override
        public Injector produce(final Dependencies dependencies, final Properties properties) throws Exception {
            return new Injector() {
                @Override
                public boolean inject(final Object bean) throws Exception {
                    injected.set(true);
                    return false;
                }
            };
        }
    }
}
