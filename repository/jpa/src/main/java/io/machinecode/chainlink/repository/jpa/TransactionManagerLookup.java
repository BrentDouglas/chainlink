package io.machinecode.chainlink.repository.jpa;

import javax.persistence.EntityManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransactionManagerLookup {

    ExtendedTransactionManager getTransactionManager(final EntityManager entityManager);
}
