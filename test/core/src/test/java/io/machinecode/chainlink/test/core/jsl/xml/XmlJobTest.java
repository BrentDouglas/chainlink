package io.machinecode.chainlink.test.core.jsl.xml;

import io.machinecode.chainlink.jsl.loader.AbstractJobLoader;
import io.machinecode.chainlink.jsl.loader.JarXmlJobLoader;
import io.machinecode.chainlink.test.core.jsl.InheritanceJobTest;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class XmlJobTest  extends InheritanceJobTest {

    private static ClassLoader classLoader = XmlJobTest.class.getClassLoader();

    @Override
    protected AbstractJobLoader createRepo() {
        return new JarXmlJobLoader(classLoader);
    }
}
