package io.machinecode.nock.jsl.api;

import io.machinecode.nock.jsl.api.type.Type;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Job {

    String getId();

    String getVersion();

    Boolean isRestartable();

    Properties getProperties();

    Listeners getListeners();

    List<Type> getTypes();
}
