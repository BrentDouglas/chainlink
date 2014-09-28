package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Command<T> extends Serializable {

    T invoke(final JGroupsRegistry registry, final Address origin) throws Throwable;
}
