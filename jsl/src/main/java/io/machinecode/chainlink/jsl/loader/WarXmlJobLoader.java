package io.machinecode.chainlink.jsl.loader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
