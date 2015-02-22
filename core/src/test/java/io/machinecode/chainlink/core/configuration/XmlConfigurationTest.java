package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.schema.xml.XmlChainlink;
import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.util.Op;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class XmlConfigurationTest extends Assert {

    @Test
    public void subsystemTest() throws Exception {
        final XmlChainlinkSubSystem that = XmlChainlinkSubSystem.read(new FileInputStream("src/test/resources/chainlink-subsystem.xml"));
        assertNotNull(that);
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        assertEquals(2, that.getDeployments().size());
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void subsystemAcceptTest() throws Exception {
        final XmlChainlinkSubSystem that = XmlChainlinkSubSystem.read(new FileInputStream("src/test/resources/chainlink-subsystem.xml"));
        assertEquals("theSubSystemConfiguration", that.getRef());
        that.accept(new XmlChainlinkSubSystem());
        assertNull(that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        assertEquals(2, that.getDeployments().size());

        that.accept(new XmlChainlinkSubSystem(), Op.values());
        assertNull(that.getRef());
        assertEquals(0, that.getConfigurationLoaders().size());
        assertEquals(0, that.getJobOperators().size());
        assertEquals(0, that.getDeployments().size());

        that.accept(XmlChainlinkSubSystem.read(new FileInputStream("src/test/resources/chainlink-subsystem.xml")), Op.values());
        assertEquals("theSubSystemConfiguration", that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        assertEquals(2, that.getDeployments().size());
    }

    @Test
    public void chainlinkTest() throws Exception {
        final XmlChainlink that = XmlChainlink.read(new FileInputStream("src/test/resources/test-chainlink.xml"));
        assertNotNull(that);
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void chainlinkAcceptTest() throws Exception {
        final XmlChainlink that = XmlChainlink.read(new FileInputStream("src/test/resources/test-chainlink.xml"));
        final XmlChainlink xml = new XmlChainlink();
        xml.setRef("asdf");
        that.accept(xml);
        assertEquals("asdf", that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());

        that.accept(new XmlChainlink(), Op.values());
        assertNull(that.getRef());
        assertEquals(0, that.getConfigurationLoaders().size());
        assertEquals(0, that.getJobOperators().size());

        that.accept(XmlChainlink.read(new FileInputStream("src/test/resources/test-chainlink.xml")), Op.values());
        assertNull(that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
    }

    @Test
    public void deploymentTest() throws Exception {
        final XmlDeployment that = XmlDeployment.read(new FileInputStream("src/test/resources/deployment.xml"));
        assertNotNull(that);
        assertEquals(1, that.getJobOperators().size());
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void deploymentAcceptTest() throws Exception {
        final XmlDeployment that = XmlDeployment.read(new FileInputStream("src/test/resources/deployment.xml"));
        that.accept(new XmlDeployment());
        assertEquals(1, that.getJobOperators().size());

        that.accept(new XmlDeployment(), Op.values());
        assertEquals(0, that.getJobOperators().size());

        that.accept(XmlDeployment.read(new FileInputStream("src/test/resources/deployment.xml")), Op.values());
        assertEquals(1, that.getJobOperators().size());
    }

    @Test
    public void jobOperatorTest() throws Exception {
        final XmlJobOperator that = XmlJobOperator.read(new FileInputStream("src/test/resources/job-operator.xml"));
        assertNotNull(that);
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void jobOperatorAcceptTest() throws Exception {
        final XmlJobOperator that = XmlJobOperator.read(new FileInputStream("src/test/resources/job-operator.xml"));
        that.accept(new XmlJobOperator());

        that.accept(new XmlJobOperator(), Op.values());

        that.accept(XmlJobOperator.read(new FileInputStream("src/test/resources/job-operator.xml")), Op.values());
    }
}
