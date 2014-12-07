package io.machinecode.chainlink.ee.wildfly;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface WildFlyConstants {

    int MANAGEMENT_API_MAJOR_VERSION = 1;
    int MANAGEMENT_API_MINOR_VERSION = 0;
    int MANAGEMENT_API_MICRO_VERSION = 0;

    String NAMESPACE_1_0 = "urn:machinecode:chainlink:1.0";

    String RESOURCE_NAME = WildFlyConstants.class.getPackage().getName() + ".LocalDescriptions";

    String SERVICE_NAME = "chainlink";

    String SUBSYSTEM_NAME = "chainlink";
    String JOB_OPERATOR = "job-operator";
    String PROPERTY = "property";

    String CLASS_LOADER_FACTORY = "class-loader-factory";
    String WORKER_FACTORY = "worker-factory";
    String EXECUTOR_FACTORY = "executor-factory";
    String REGISTRY_FACTORY = "registry-factory";
    String MARSHALLING_PROVIDER_FACTORY = "marshalling-provider-factory";
    String EXECUTION_REPOSITORY_FACTORY = "execution-repository-factory";
    String TRANSACTION_MANAGER_FACTORY = "transaction-manager-factory";
    String MBEAN_SERVER_FACTORY = "mbean-server-factory";
    String JOB_LOADER_FACTORY = "job-loader-factory";
    String ARTIFACT_LOADER_FACTORY = "artifact-loader-factory";
    String INJECTOR_FACTORY = "injector-factory";
    String SECURITY_CHECK_FACTORY = "security-check-factory";

    String CLASS = "class";
    String REF = "ref";
    String FACTORY = "factory";
    String JNDI_NAME = "jndi-name";

    String NAME = "name";
    String VALUE = "value";
}
