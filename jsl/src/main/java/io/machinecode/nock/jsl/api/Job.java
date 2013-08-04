package io.machinecode.nock.jsl.api;

import io.machinecode.nock.jsl.api.execution.Execution;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public interface Job {

    String ELEMENT = "job";

    String NAMESPACE = "http://xmlns.jcp.org/xml/ns/javaee";

    String getId();

    String getVersion();

    boolean isRestartable();

    Properties getProperties();

    Listeners getListeners();

    List<? extends Execution> getExecutions();
}
