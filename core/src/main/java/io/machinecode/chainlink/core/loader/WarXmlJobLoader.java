package io.machinecode.chainlink.core.loader;

import javax.xml.bind.JAXBException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WarXmlJobLoader extends XmlJobLoader {

    public WarXmlJobLoader(final ClassLoader loader) throws JAXBException {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "WEB-INF/classes/META-INF/batch-jobs/";
    }
}
