package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface RegistryConfiguration extends RepositoryConfiguration {

    ExecutionRepository getRepository();
}
