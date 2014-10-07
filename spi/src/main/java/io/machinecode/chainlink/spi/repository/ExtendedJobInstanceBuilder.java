package io.machinecode.chainlink.spi.repository;

import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedJobInstanceBuilder<T extends ExtendedJobInstanceBuilder<T>> {

    T setJobInstanceId(final long jobInstanceId);

    T setJobName(final String jobName);

    T setJslName(final String jslName);

    T setCreateTime(final Date createTime);

    ExtendedJobInstance build();
}
