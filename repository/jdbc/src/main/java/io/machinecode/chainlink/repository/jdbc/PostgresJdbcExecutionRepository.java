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
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PostgresJdbcExecutionRepository extends JdbcExecutionRepository {

    public PostgresJdbcExecutionRepository(final DataSource dataSource, final String username, final String password) {
        super(dataSource, username, password);
    }

    @Override
    protected void setLargeObject(final PreparedStatement statement, final int index, final byte[] bytes) throws SQLException {
        if (bytes == null) {
            statement.setNull(index, Types.BIGINT);
            return;
        }
        LargeObjectManager manager = ((PGConnection)statement.getConnection()).getLargeObjectAPI();
        long oid = manager.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
        LargeObject lob = manager.open(oid, LargeObjectManager.WRITE);
        lob.write(bytes);
        statement.setLong(index, oid);
    }

    @Override
    protected Serializable getLargeObject(final ResultSet result, final int index) throws Exception {
        LargeObjectManager manager = ((PGConnection)result.getStatement().getConnection()).getLargeObjectAPI();
        long oid = result.getLong(index);
        if (oid == 0) {
            return null;
        }
        LargeObject lob = manager.open(oid, LargeObjectManager.READ);
        return _read(lob.getInputStream());
    }
}
