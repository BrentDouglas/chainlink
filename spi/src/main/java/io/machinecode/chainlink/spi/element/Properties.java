package io.machinecode.chainlink.spi.element;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Properties extends Element {

    String ELEMENT = "properties";

    List<? extends Property> getProperties();

    String getPartition();
}
