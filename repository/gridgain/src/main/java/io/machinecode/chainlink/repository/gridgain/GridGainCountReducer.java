package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import org.gridgain.grid.lang.GridReducer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class GridGainCountReducer implements GridReducer<Map.Entry<Long, ExtendedJobInstance>, Integer> {
    private int count = 0;

    @Override
    public boolean collect(@Nullable final Map.Entry<Long, ExtendedJobInstance> entry) {
        ++count;
        return true;
    }

    @Override
    public Integer reduce() {
        return count;
    }
}
