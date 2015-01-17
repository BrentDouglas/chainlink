package io.machinecode.chainlink.core.configuration.def;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobOperatorDef<Dec extends DeclarationDef, Prop extends PropertyDef> {

    String getName();

    void setName(final String name);

    String getRef();

    void setRef(final String ref);

    Dec getClassLoader();

    void setClassLoader(final Dec classLoader);

    Dec getTransactionManager();

    void setTransactionManager(final Dec transactionManager);

    Dec getMarshalling();

    void setMarshalling(final Dec marshalling);

    Dec getMBeanServer();

    void setMBeanServer(final Dec mBeanServer);

    List<Dec> getJobLoaders();

    void setJobLoaders(final List<Dec> jobLoaders);

    List<Dec> getArtifactLoaders();

    void setArtifactLoaders(final List<Dec> artifactLoaders);

    List<Dec> getInjectors();

    void setInjectors(final List<Dec> injectors);

    List<Dec> getSecurities();

    void setSecurities(final List<Dec> securities);

    Dec getExecutionRepository();

    void setExecutionRepository(final Dec executionRepository);

    Dec getRegistry();

    void setRegistry(final Dec registry);

    Dec getTransport();

    void setTransport(final Dec transport);

    Dec getExecutor();

    void setExecutor(final Dec executor);

    List<Prop> getProperties();

    void setProperties(final List<Prop> properties);
}
