package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.partition.Mapper;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperValidator extends PropertyReferenceValidator<Mapper> {

    public static final MapperValidator INSTANCE = new MapperValidator();

    protected MapperValidator() {
        super(Mapper.ELEMENT);
    }
}
