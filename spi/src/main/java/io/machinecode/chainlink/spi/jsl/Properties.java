package io.machinecode.chainlink.spi.jsl;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Properties extends Element {

    String ELEMENT = "properties";

    List<? extends Property> getProperties();

    String getPartition();
}
