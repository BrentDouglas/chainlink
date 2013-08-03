package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperValidator extends PropertyReferenceValidator<Mapper> {

    public static final MapperValidator INSTANCE = new MapperValidator();

    protected MapperValidator() {
        super("mapper");
    }
}
