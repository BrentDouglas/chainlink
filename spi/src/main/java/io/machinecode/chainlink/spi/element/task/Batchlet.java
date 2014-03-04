package io.machinecode.chainlink.spi.element.task;

import io.machinecode.chainlink.spi.element.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Batchlet extends Task, PropertyReference {

    String ELEMENT = "batchlet";

}
