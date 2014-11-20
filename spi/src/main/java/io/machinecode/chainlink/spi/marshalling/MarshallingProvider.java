package io.machinecode.chainlink.spi.marshalling;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public interface MarshallingProvider {

    Cloner getCloner();

    Marshaller getMarshaller();

    Unmarshaller getUnmarshaller();
}
