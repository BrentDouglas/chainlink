package io.machinecode.chainlink.spi.element;

import io.machinecode.chainlink.spi.element.execution.Execution;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Job extends PropertiesElement {

    String ELEMENT = "job";

    String SCHEMA_URL = "http://xmlns.jcp.org/xml/ns/javaee/jobXML_1_0.xsd";
    String NAMESPACE = "http://xmlns.jcp.org/xml/ns/javaee";

    String getId();

    String getVersion();

    String getRestartable();

    Listeners getListeners();

    List<? extends Execution> getExecutions();
}
