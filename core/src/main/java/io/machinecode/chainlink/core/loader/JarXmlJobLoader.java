package io.machinecode.chainlink.core.loader;

import javax.xml.bind.JAXBException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JarXmlJobLoader extends XmlJobLoader {

    public JarXmlJobLoader(final ClassLoader loader) throws JAXBException {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "META-INF/batch-jobs/";
    }
}
