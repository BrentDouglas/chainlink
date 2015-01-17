package io.machinecode.chainlink.core.configuration.def;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface DeclarationDef extends NamedDef {

    String getRef();

    void setRef(final String ref);
}
