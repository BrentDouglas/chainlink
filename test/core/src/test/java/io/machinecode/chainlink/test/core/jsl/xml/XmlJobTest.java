package io.machinecode.chainlink.test.core.jsl.xml;

import io.machinecode.chainlink.jsl.xml.loader.JarXmlJobLoader;
import io.machinecode.chainlink.test.core.jsl.InheritanceJobTest;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class XmlJobTest extends InheritanceJobTest {

    private static ClassLoader classLoader = XmlJobTest.class.getClassLoader();

    @Override
    protected JarXmlJobLoader createRepo() {
        return new JarXmlJobLoader(classLoader);
    }
}
