package io.machinecode.nock.spi;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobDescriptor {

    long getJobId();

    List<? extends StepDescriptor> getStepDescriptors();
}
