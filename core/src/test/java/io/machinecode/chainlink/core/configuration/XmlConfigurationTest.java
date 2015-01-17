package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.configuration.xml.XmlChainlink;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;
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
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void chainlinkTest() throws Exception {
        final XmlChainlink that = XmlChainlink.read(new FileInputStream("src/test/resources/test-chainlink.xml"));
        assertNotNull(that);
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void deploymentTest() throws Exception {
        final XmlDeployment that = XmlDeployment.read(new FileInputStream("src/test/resources/deployment.xml"));
        assertNotNull(that);
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void jobOperatorTest() throws Exception {
        final XmlJobOperator that = XmlJobOperator.read(new FileInputStream("src/test/resources/job-operator.xml"));
        assertNotNull(that);
        that.write(new ByteArrayOutputStream());
    }
}
