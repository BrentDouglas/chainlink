package io.machinecode.chainlink.inject.core;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
