/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.repository.jdbc;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PostgresJdbcRepository extends JdbcRepository {

    public PostgresJdbcRepository(final DataSource dataSource, final String username, final String password) {
        super(dataSource, username, password);
    }

    @Override
    protected void setLargeObject(final PreparedStatement statement, final int index, final byte[] bytes) throws SQLException {
        if (bytes == null) {
            statement.setNull(index, Types.BIGINT);
            return;
        }
        final LargeObjectManager manager = ((PGConnection)statement.getConnection()).getLargeObjectAPI();
        final long oid = manager.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
        final LargeObject lob = manager.open(oid, LargeObjectManager.WRITE);
        lob.write(bytes);
        statement.setLong(index, oid);
    }

    @Override
    protected Serializable getLargeObject(final ResultSet result, final int index) throws Exception {
        final LargeObjectManager manager = ((PGConnection)result.getStatement().getConnection()).getLargeObjectAPI();
        final long oid = result.getLong(index);
        if (oid == 0) {
            return null;
        }
        final LargeObject lob = manager.open(oid, LargeObjectManager.READ);
        return _read(lob.getInputStream());
    }
}
