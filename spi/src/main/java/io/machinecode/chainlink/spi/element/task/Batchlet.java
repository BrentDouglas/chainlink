package io.machinecode.chainlink.spi.element.task;

import io.machinecode.chainlink.spi.element.PropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Batchlet extends Task, PropertyReference {

    String ELEMENT = "batchlet";

}
