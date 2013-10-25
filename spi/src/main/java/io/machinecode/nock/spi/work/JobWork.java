package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobWork extends Job, Work, Planned, Serializable {

    void before(final Transport transport, final Context context) throws Exception;

    Plan run(final Transport transport, final Context context) throws Exception;

    void after(final Transport transport, final Context context) throws Exception;

    /**
     * @param next The id of the element to extract
     * @return The element in the job with id {@param next}
     */
    ExecutionWork next(final String next);

    /**
     * @param element The element to get the properties for
     * @return A list of al properties
     */
    List<? extends Pair<String, String>> properties(final Element element);
}
