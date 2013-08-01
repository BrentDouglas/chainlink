package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.Analyser;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserValidator extends PropertyReferenceValidator<Analyser> {

    public static final AnalyserValidator INSTANCE = new AnalyserValidator();

    protected AnalyserValidator() {
        super("analyser");
    }
}
