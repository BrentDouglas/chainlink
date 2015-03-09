package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobOperatorSchema<Prop extends PropertySchema> {

    String getName();

    String getRef();

    String getClassLoader();

    String getTransactionManager();

    String getMarshalling();

    String getMBeanServer();

    List<String> getJobLoaders();

    List<String> getArtifactLoaders();

    List<String> getSecurities();

    String getRepository();

    String getRegistry();

    String getTransport();

    String getExecutor();

    List<Prop> getProperties();
}
