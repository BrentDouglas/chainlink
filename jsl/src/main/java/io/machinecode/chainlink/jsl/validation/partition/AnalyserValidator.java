package io.machinecode.chainlink.jsl.validation.partition;

import io.machinecode.chainlink.spi.element.partition.Analyser;
import io.machinecode.chainlink.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserValidator extends PropertyReferenceValidator<Analyser> {

    public static final AnalyserValidator INSTANCE = new AnalyserValidator();

    protected AnalyserValidator() {
        super(Analyser.ELEMENT);
    }
}
