package io.machinecode.chainlink.jsl.validation.partition;

import io.machinecode.chainlink.spi.element.partition.Mapper;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperValidator extends PropertyReferenceValidator<Mapper> {

    public static final MapperValidator INSTANCE = new MapperValidator();

    protected MapperValidator() {
        super(Mapper.ELEMENT);
    }
}
