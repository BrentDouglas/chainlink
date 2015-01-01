package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.element.partition.Analyser;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentAnalyser extends FluentPropertyReference<FluentAnalyser> implements Analyser {
    @Override
    public FluentAnalyser copy() {
        return copy(new FluentAnalyser());
    }
}
