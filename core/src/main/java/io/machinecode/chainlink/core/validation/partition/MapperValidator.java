package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.partition.Mapper;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class MapperValidator extends PropertyReferenceValidator<Mapper> {

    public static final MapperValidator INSTANCE = new MapperValidator();

    protected MapperValidator() {
        super(Mapper.ELEMENT);
    }
}
