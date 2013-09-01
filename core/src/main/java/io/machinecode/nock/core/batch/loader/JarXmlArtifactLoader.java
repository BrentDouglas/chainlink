package io.machinecode.nock.core.batch.loader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JarXmlArtifactLoader extends XmlArtifactLoader {

    public JarXmlArtifactLoader(final ClassLoader loader) {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "META-INF/";
    }
}
