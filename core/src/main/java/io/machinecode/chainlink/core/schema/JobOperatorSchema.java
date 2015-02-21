package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobOperatorSchema<Dec extends DeclarationSchema, Prop extends PropertySchema> {

    String getName();

    String getRef();

    Dec getClassLoader();

    Dec getTransactionManager();

    Dec getMarshalling();

    Dec getMBeanServer();

    List<Dec> getJobLoaders();

    List<Dec> getArtifactLoaders();

    List<Dec> getSecurities();

    Dec getRepository();

    Dec getRegistry();

    Dec getTransport();

    Dec getExecutor();

    List<Prop> getProperties();
}
