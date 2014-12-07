package io.machinecode.chainlink.ee.wildfly.configuration;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class Config {
    public final String classLoaderFactory;
    public final String workerFactory;
    public final String executorFactory;
    public final String registryFactory;
    public final String marshallingProviderFactory;
    public final String executionRepositoryFactory;
    public final String transactionManagerFactory;
    public final String mBeanServerFactory;
    public final String[] jobLoaderFactories;
    public final String[] artifactLoaderFactories;
    public final String[] injectorFactories;
    public final String[] securityCheckFactories;

    public Config(final String classLoaderFactory, final String workerFactory, final String executorFactory, final String registryFactory,
                  final String marshallingProviderFactory, final String executionRepositoryFactory, final String transactionManagerFactory,
                  final String mBeanServerFactory, final String[] jobLoaderFactories, final String[] artifactLoaderFactories,
                  final String[] injectorFactories, final String[] securityCheckFactories) {
        this.classLoaderFactory = classLoaderFactory;
        this.workerFactory = workerFactory;
        this.executorFactory = executorFactory;
        this.registryFactory = registryFactory;
        this.marshallingProviderFactory = marshallingProviderFactory;
        this.executionRepositoryFactory = executionRepositoryFactory;
        this.transactionManagerFactory = transactionManagerFactory;
        this.mBeanServerFactory = mBeanServerFactory;
        this.jobLoaderFactories = jobLoaderFactories;
        this.artifactLoaderFactories = artifactLoaderFactories;
        this.injectorFactories = injectorFactories;
        this.securityCheckFactories = securityCheckFactories;
    }
}
