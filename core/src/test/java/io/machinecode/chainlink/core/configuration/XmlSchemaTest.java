package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.XmlSchema;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.util.Tccl;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class XmlSchemaTest extends Assert {

    @Test
    public void testSubSystemCopy() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final XmlChainlinkSubSystem in = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/subsystem.xml"));
        final XmlChainlinkSubSystem out = XmlSchema.xmlSubSystem(in);
        compareSubsystems(in, out);
    }

    private static void compareSubsystems(final XmlChainlinkSubSystem in, final XmlChainlinkSubSystem out) {
        assertEquals(1, in.getConfigurationLoaders().size());
        assertEquals(1, out.getConfigurationLoaders().size());
        assertEquals(in.getConfigurationLoaders().get(0).getName(), out.getConfigurationLoaders().get(0).getName());
        assertEquals(in.getConfigurationLoaders().get(0).getRef(), out.getConfigurationLoaders().get(0).getRef());

        assertEquals(2, in.getJobOperators().size());
        assertEquals(2, out.getJobOperators().size());
        assertEquals(in.getJobOperators().get(0).getName(), out.getJobOperators().get(0).getName());
        assertEquals(in.getJobOperators().get(0).getRef(), out.getJobOperators().get(0).getRef());
        assertEquals(in.getJobOperators().get(1).getName(), out.getJobOperators().get(1).getName());
        assertNull(in.getJobOperators().get(1).getRef());
        assertNull(out.getJobOperators().get(1).getRef());

        assertEquals(2, in.getDeployments().size());
        assertEquals(2, out.getDeployments().size());
        assertEquals(in.getDeployments().get(0).getName(), out.getDeployments().get(0).getName());
        assertNull(in.getDeployments().get(0).getRef());
        assertNull(out.getDeployments().get(0).getRef());
        assertEquals(in.getDeployments().get(1).getName(), out.getDeployments().get(1).getName());
        assertEquals(in.getDeployments().get(1).getRef(), out.getDeployments().get(1).getRef());

        //TODO Compare these better
    }

    @Test
    public void testSubSystemWrite() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final XmlChainlinkSubSystem in = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/subsystem.xml"));
        final ByteArrayInputStream stream = XmlSchema.writeSubSystem(in);
        final XmlChainlinkSubSystem out = XmlChainlinkSubSystem.read(stream);
        compareSubsystems(in, out);
    }

    @Test
    public void testDeploymentCopy() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final XmlDeployment in = XmlDeployment.read(tccl.getResourceAsStream("test/xml/deployment.xml"));
        final XmlDeployment out = XmlSchema.xmlDeployment(in);
        //TODO Compare these
    }

    @Test
    public void testDeploymentWrite() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final XmlDeployment in = XmlDeployment.read(tccl.getResourceAsStream("test/xml/deployment.xml"));
        final ByteArrayInputStream stream = XmlSchema.writeDeployment(in);
        final XmlDeployment out = XmlDeployment.read(stream);
        //TODO Compare these
    }

    @Test
    public void testJobOperatorCopy() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final XmlJobOperator in = XmlJobOperator.read(tccl.getResourceAsStream("test/xml/job-operator.xml"));
        final XmlJobOperator out = XmlSchema.xmlJobOperator(in);
        //TODO Compare these
    }

    @Test
    public void testJobOperatorWrite() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final XmlJobOperator in = XmlJobOperator.read(tccl.getResourceAsStream("test/xml/job-operator.xml"));
        final ByteArrayInputStream stream = XmlSchema.writeJobOperator(in);
        final XmlJobOperator out = XmlJobOperator.read(stream);
        //TODO Compare these
    }
}
