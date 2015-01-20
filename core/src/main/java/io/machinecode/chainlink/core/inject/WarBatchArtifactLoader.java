package io.machinecode.chainlink.core.inject;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WarBatchArtifactLoader extends BatchArtifactLoader {

    public WarBatchArtifactLoader(final ClassLoader loader) throws JAXBException, IOException {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "WEB-INF/classes/META-INF/";
    }
}
