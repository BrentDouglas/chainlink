package io.machinecode.chainlink.core.jsl.xml;

import io.machinecode.chainlink.core.loader.JarXmlJobLoader;
import io.machinecode.chainlink.core.jsl.InheritanceJobTest;

import javax.xml.bind.JAXBException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class XmlJobTest extends InheritanceJobTest {

    private static ClassLoader classLoader = XmlJobTest.class.getClassLoader();

    @Override
    protected JarXmlJobLoader createRepo() throws JAXBException {
        return new JarXmlJobLoader(classLoader);
    }
}
