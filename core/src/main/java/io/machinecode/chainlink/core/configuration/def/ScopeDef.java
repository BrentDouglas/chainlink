package io.machinecode.chainlink.core.configuration.def;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ScopeDef<Dec extends DeclarationDef, Prop extends PropertyDef, Job extends JobOperatorDef<Dec, Prop>>  {

    String getRef();

    void setRef(final String ref);

    List<Dec> getArtifactLoaders();

    void setArtifactLoaders(final List<Dec> artifactLoaders);

    List<Job> getJobOperators();

    void setJobOperators(final List<Job> jobOperators);
}
