package io.machinecode.nock.core.batch.loader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WarXmlArtifactLoader extends XmlArtifactLoader {

    public WarXmlArtifactLoader(final ClassLoader loader) {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "WEB-INF/classes/META-INF/";
    }
}
