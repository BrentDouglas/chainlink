package io.machinecode.chainlink.ee.wildfly;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
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

    String ID = "id";
    String WORKER_FACTORY = "worker-factory";
    String MARSHALLER_FACTORY = "marshaller-factory";
    String EXECUTION_REPOSITORY_FACTORY = "execution-repository-factory";
    String JOB_LOADER_FACTORY = "job-loader-factory";
    String ARTIFACT_LOADER_FACTORY = "artifact-loader-factory";
    String INJECTOR_FACTORY = "injector-factory";
    String SECURITY_CHECK_FACTORY = "security-check-factory";

    String CLASS = "class";

    String NAME = "name";
    String VALUE = "value";
}
