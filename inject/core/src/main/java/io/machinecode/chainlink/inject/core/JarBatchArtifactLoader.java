package io.machinecode.chainlink.inject.core;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JarBatchArtifactLoader extends BatchArtifactLoader {

    public JarBatchArtifactLoader(final ClassLoader loader) {
        super(loader);
    }

    @Override
    public String getPrefix() {
        return "META-INF/";
    }
}
