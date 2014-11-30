package io.machinecode.chainlink.jsl.xml.loader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
