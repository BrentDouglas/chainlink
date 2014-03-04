package io.machinecode.chainlink.jsl.validation.partition;

import io.machinecode.chainlink.spi.element.partition.Reducer;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerValidator extends PropertyReferenceValidator<Reducer> {

    public static final ReducerValidator INSTANCE = new ReducerValidator();

    protected ReducerValidator() {
        super(Reducer.ELEMENT);
    }
}
