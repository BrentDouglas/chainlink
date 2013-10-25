package io.machinecode.nock.spi.element;

import io.machinecode.nock.spi.PropertiesElement;
import io.machinecode.nock.spi.element.execution.Execution;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
