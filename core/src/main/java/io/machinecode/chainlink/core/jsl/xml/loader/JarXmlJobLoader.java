package io.machinecode.chainlink.core.jsl.xml.loader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JarXmlJobLoader extends XmlJobLoader {

    public JarXmlJobLoader(final ClassLoader loader) {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "META-INF/batch-jobs/";
    }
}
