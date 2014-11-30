package io.machinecode.chainlink.repository.jpa;

import javax.persistence.EntityManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface TransactionManagerLookup {

    ExtendedTransactionManager getTransactionManager(final EntityManager entityManager);
}
