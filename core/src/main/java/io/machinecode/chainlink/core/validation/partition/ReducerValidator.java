package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.jsl.partition.Reducer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ReducerValidator extends PropertyReferenceValidator<Reducer> {

    public static final ReducerValidator INSTANCE = new ReducerValidator();

    protected ReducerValidator() {
        super(Reducer.ELEMENT);
    }
}
