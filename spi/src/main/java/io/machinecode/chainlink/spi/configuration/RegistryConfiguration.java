package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RegistryConfiguration extends RepositoryConfiguration {

    ExecutionRepository getRepository();
}
