package io.machinecode.chainlink.jsl.xml.loader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
