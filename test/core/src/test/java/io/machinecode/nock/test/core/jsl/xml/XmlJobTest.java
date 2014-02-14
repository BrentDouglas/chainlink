package io.machinecode.nock.test.core.jsl.xml;

import io.machinecode.nock.jsl.loader.AbstractJobLoader;
import io.machinecode.nock.jsl.loader.JarXmlJobLoader;
import io.machinecode.nock.test.core.jsl.InheritanceJobTest;

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
