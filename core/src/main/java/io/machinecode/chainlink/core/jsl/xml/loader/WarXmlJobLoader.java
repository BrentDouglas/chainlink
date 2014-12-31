package io.machinecode.chainlink.core.jsl.xml.loader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WarXmlJobLoader extends XmlJobLoader {

    public WarXmlJobLoader(final ClassLoader loader) {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "WEB-INF/classes/META-INF/batch-jobs/";
    }
}
