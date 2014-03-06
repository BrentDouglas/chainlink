package io.machinecode.chainlink.repository.jpa;

import javax.persistence.EntityManagerFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface EntityManagerLookup {

    EntityManagerFactory getEntityManagerFactory();
}
