package io.machinecode.chainlink.rt.wildfly;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface WildFlyConstants {

    int MANAGEMENT_API_MAJOR_VERSION = 1;
    int MANAGEMENT_API_MINOR_VERSION = 0;
    int MANAGEMENT_API_MICRO_VERSION = 0;

    String NAMESPACE_1_0 = "urn:jboss:domain:chainlink:1.0";

    String RESOURCE_NAME = WildFlyConstants.class.getPackage().getName() + ".LocalDescriptions";

    String SERVICE_NAME = "chainlink";
    String MODEL = "model";

    String SUBSYSTEM_NAME = "chainlink";
    String DEPLOYMENT = "deployment";
    String JOB_OPERATOR = "job-operator";
    String PROPERTY = "property";

    String CLASS_LOADER = "class-loader";
    String EXECUTOR = "executor";
    String TRANSPORT = "transport";
    String REGISTRY = "registry";
    String MARSHALLING = "marshalling";
    String REPOSITORY = "repository";
    String TRANSACTION_MANAGER = "transaction-manager";
    String MBEAN_SERVER = "mbean-server";
    String JOB_LOADERS = "job-loaders";
    String ARTIFACT_LOADERS = "artifact-loaders";
    String SECURITIES = "securities";

    String CONFIGURATION_LOADERS = "configuration-loaders";
    String REF = "ref";

    String NAME = "name";
    String VALUE = "value";
}
