package io.machinecode.chainlink.spi.schema;

import io.machinecode.chainlink.spi.management.Mutable;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableJobOperatorSchema<Dec extends MutableDeclarationSchema, Prop extends MutablePropertySchema>
        extends JobOperatorSchema<Dec,Prop>, Mutable<JobOperatorSchema<?,?>> {

    void setName(final String name);

    void setRef(final String ref);

    void setClassLoader(final Dec classLoader);

    void setTransactionManager(final Dec transactionManager);

    void setMarshalling(final Dec marshalling);

    void setMBeanServer(final Dec mBeanServer);

    void setJobLoaders(final List<Dec> jobLoaders);

    void setArtifactLoaders(final List<Dec> artifactLoaders);

    void setInjectors(final List<Dec> injectors);

    void setSecurities(final List<Dec> securities);

    void setExecutionRepository(final Dec executionRepository);

    void setRegistry(final Dec registry);

    void setTransport(final Dec transport);

    void setExecutor(final Dec executor);

    void setProperties(final List<Prop> properties);
}
