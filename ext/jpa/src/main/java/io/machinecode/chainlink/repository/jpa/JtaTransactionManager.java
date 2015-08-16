/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.repository.jpa;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JtaTransactionManager implements ExtendedTransactionManager {

    private final TransactionManager delegate;

    public JtaTransactionManager(final TransactionManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isResourceLocal() {
        return false;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        delegate.begin();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
        delegate.commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {
        delegate.rollback();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        delegate.setRollbackOnly();
    }

    @Override
    public int getStatus() throws SystemException {
        return delegate.getStatus();
    }

    @Override
    public Transaction getTransaction() throws SystemException {
        return delegate.getTransaction();
    }

    @Override
    public void setTransactionTimeout(final int seconds) throws SystemException {
        delegate.setTransactionTimeout(seconds);
    }

    @Override
    public Transaction suspend() throws SystemException {
        return delegate.suspend();
    }

    @Override
    public void resume(final Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        delegate.resume(transaction);
    }
}
