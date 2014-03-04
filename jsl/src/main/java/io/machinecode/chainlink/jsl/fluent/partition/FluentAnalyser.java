package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.element.partition.Analyser;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentAnalyser extends FluentPropertyReference<FluentAnalyser> implements Analyser {
    @Override
    public FluentAnalyser copy() {
        return copy(new FluentAnalyser());
    }
}
