package io.machinecode.chainlink.coherence.test.transport;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.core.transport.TransportTest;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Ignore //TODO Work out how to configure these nodes to form a cluster
@RunWith(CoherenceTestRunner.class)
public class CoherenceTransportTest extends TransportTest {
    @Override
    protected TestTransportFactory createFactory() {
        throw new IllegalStateException();
    }

    private TestTransportFactory createFactory(final CoherenceClassLoader loader, final String config) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        return TestTransportFactory.class.cast(loader.loadClass(TestCoherenceTransportFactory.class.getName()).getConstructor(String.class).newInstance(config));
    }

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        final CoherenceClassLoader loader = new CoherenceClassLoader(CoherenceTransportTest.class.getClassLoader());
        model.getClassLoader().setValue(loader);
        if (firstFactory == null) {
            firstFactory = createFactory(loader, "first.xml");
        }
        model.getTransport().setFactory(firstFactory);
    }

    protected void visitSecondJobOperatorModel(final JobOperatorModel model) throws Exception {
        final CoherenceClassLoader loader = new CoherenceClassLoader(CoherenceTransportTest.class.getClassLoader());
        model.getClassLoader().setValue(loader);
        if (secondFactory == null) {
            secondFactory = createFactory(loader, "second.xml");
        }
        model.getTransport().setFactory(secondFactory);
    }

    protected void visitThirdJobOperatorModel(final JobOperatorModel model) throws Exception {
        final CoherenceClassLoader loader = new CoherenceClassLoader(CoherenceTransportTest.class.getClassLoader());
        model.getClassLoader().setValue(loader);
        if (thirdFactory == null) {
            thirdFactory = createFactory(loader, "third.xml");
        }
        model.getTransport().setFactory(thirdFactory);
    }
}
