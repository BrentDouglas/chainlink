package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.partition.Analyser;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class AnalyserValidator extends PropertyReferenceValidator<Analyser> {

    public static final AnalyserValidator INSTANCE = new AnalyserValidator();

    protected AnalyserValidator() {
        super(Analyser.ELEMENT);
    }
}
