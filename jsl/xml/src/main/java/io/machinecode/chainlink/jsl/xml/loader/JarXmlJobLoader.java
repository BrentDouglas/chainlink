package io.machinecode.chainlink.jsl.xml.loader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
