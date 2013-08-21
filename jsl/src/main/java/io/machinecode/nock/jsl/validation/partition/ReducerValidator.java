package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.spi.element.partition.Reducer;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerValidator extends PropertyReferenceValidator<Reducer> {

    public static final ReducerValidator INSTANCE = new ReducerValidator();

    protected ReducerValidator() {
        super(Reducer.ELEMENT);
    }
}
