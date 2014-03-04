package io.machinecode.chainlink.core.batch.loader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WarBatchArtifactLoader extends BatchArtifactLoader {

    public WarBatchArtifactLoader(final ClassLoader loader) {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "WEB-INF/classes/META-INF/";
    }
}
