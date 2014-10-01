package io.machinecode.chainlink.repository.jdbc;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.repository.core.JobExecutionImpl;
import io.machinecode.chainlink.repository.core.JobInstanceImpl;
import io.machinecode.chainlink.repository.core.MetricImpl;
import io.machinecode.chainlink.repository.core.PartitionExecutionImpl;
import io.machinecode.chainlink.repository.core.StepExecutionImpl;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.util.Messages;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdbcExecutionRepository implements ExecutionRepository {

    private final DataSource dataSource;
    private final String username;
    private final String password;

    public JdbcExecutionRepository(final DataSource dataSource, final String username, final String password) {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
    }

    public static JdbcExecutionRepository create(final DataSourceLookup lookup, final String username, final String password) {
        final DataSource dataSource = lookup.getDataSource();
        try {
            final Connection connection = username == null || password == null
                    ? dataSource.getConnection()
                    : dataSource.getConnection(username, password);
            final String url = connection.getMetaData().getURL();
            if (url.startsWith("jdbc:postgresql")) {
                return new PostgresJdbcExecutionRepository(dataSource, username, password);
            } else {
                return new JdbcExecutionRepository(dataSource, username, password);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e); //TODO
        }
    }

    public static JdbcExecutionRepository create(final DataSourceLookup lookup) {
        return create(lookup, null, null);
    }

    private Connection _connection() throws SQLException {
        return this.username == null || this.password == null
                ? dataSource.getConnection()
                : dataSource.getConnection(username, password);
    }

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(insertJobInstance(), Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, jobId);
            statement.setString(2, jslName);
            statement.setTimestamp(3, new Timestamp(timestamp.getTime()));
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            final ResultSet result = statement.getGeneratedKeys();
            if (!result.next()) {
                throw new IllegalStateException(); //TODO
            }
            final long jobInstanceId = result.getLong(1);
            statement.close();
            return new JobInstanceImpl.Builder()
                    .setInstanceId(jobInstanceId)
                    .setCreatedTime(timestamp)
                    .setJobName(jobId)
                    .setJslName(jslName)
                    .build();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final JobExecutionImpl jobExecution = _createJobExecution(connection, jobInstance, parameters, timestamp);
            connection.commit();
            return jobExecution;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public JobExecutionImpl _createJobExecution(final Connection connection, final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        final PreparedStatement statement = connection.prepareStatement(insertJobExecution(), Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, jobInstance.getInstanceId());
        statement.setString(2, jobInstance.getJobName());
        statement.setString(3, BatchStatus.STARTING.name());
        final Timestamp ts = new Timestamp(timestamp.getTime());
        statement.setTimestamp(4, ts);
        statement.setTimestamp(5, ts);
        if (statement.executeUpdate() == 0) {
            throw new IllegalStateException(); //TODO
        }
        final ResultSet result = statement.getGeneratedKeys();
        if (!result.next()) {
            throw new IllegalStateException(); //TODO
        }
        final long jobExecutionId = result.getLong(1);
        statement.close();
        if (parameters != null) {
            for (final String key : parameters.stringPropertyNames()) {
                final PreparedStatement ms = connection.prepareStatement(insertProperty(), Statement.RETURN_GENERATED_KEYS);
                ms.setString(1, key);
                ms.setString(2, parameters.getProperty(key));
                if (ms.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                final ResultSet mr = ms.getGeneratedKeys();
                if (!mr.next()) {
                    throw new IllegalStateException(); //TODO
                }
                final long propertyId = mr.getLong(1);
                ms.close();

                final PreparedStatement sms = connection.prepareStatement(insertPropertyToJobExecution());
                sms.setLong(1, jobExecutionId);
                sms.setLong(2, propertyId);
                if (sms.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                sms.close();

            }
        }
        connection.commit();
        return new JobExecutionImpl.Builder()
                .setJobInstanceId(jobInstance.getInstanceId())
                .setJobExecutionId(jobExecutionId)
                .setJobName(jobInstance.getJobName())
                .setBatchStatus(BatchStatus.STARTING)
                .setParameters(parameters)
                .setCreatedTime(timestamp)
                .setUpdatedTime(timestamp)
                .build();
    }

    @Override
    public ExtendedStepExecution createStepExecution(final ExtendedJobExecution jobExecution, final String stepName, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(insertStepExecution(), Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, jobExecution.getExecutionId());
            statement.setString(2, stepName);
            statement.setString(3, BatchStatus.STARTING.name());
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(4, ts);
            statement.setTimestamp(5, ts);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            final ResultSet result = statement.getGeneratedKeys();
            if (!result.next()) {
                throw new IllegalStateException(); //TODO
            }
            final long stepExecutionId = result.getLong(1);
            statement.close();
            for (final Metric.MetricType type : Metric.MetricType.values()) {
                final PreparedStatement ms = connection.prepareStatement(insertMetric(), Statement.RETURN_GENERATED_KEYS);
                ms.setString(1, type.name());
                ms.setLong(2, 0);
                if (ms.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                final ResultSet mr = ms.getGeneratedKeys();
                if (!mr.next()) {
                    throw new IllegalStateException();
                }
                final long metricId = mr.getLong(1);
                ms.close();

                final PreparedStatement sms = connection.prepareStatement(insertMetricToStepExecution());
                sms.setLong(1, stepExecutionId);
                sms.setLong(2, metricId);
                sms.setString(3, type.name());
                if (sms.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                sms.close();
            }
            connection.commit();
            return new StepExecutionImpl.Builder()
                    .setJobExecutionId(jobExecution.getExecutionId())
                    .setStepExecutionId(stepExecutionId)
                    .setStepName(stepName)
                    .setBatchStatus(BatchStatus.STARTING)
                    .setCreatedTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setMetrics(MetricImpl.empty())
                    .build();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }



    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            final byte[] bytesPersistentUserData = _bytes(persistentUserData);
            final byte[] bytesReaderCheckpoint = _bytes(readerCheckpoint);
            final byte[] bytesWriterCheckpoint = _bytes(writerCheckpoint);
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(insertPartitionExecution(), Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, stepExecutionId);
            statement.setInt(2, partitionId);
            statement.setString(3, BatchStatus.STARTING.name());
            this.setLargeObject(statement, 4, bytesPersistentUserData);
            this.setLargeObject(statement, 5, bytesReaderCheckpoint);
            this.setLargeObject(statement, 6, bytesWriterCheckpoint);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(7, ts);
            statement.setTimestamp(8, ts);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            final ResultSet result = statement.getGeneratedKeys();
            if (!result.next()) {
                throw new IllegalStateException(); //TODO
            }
            final long partitionExecutionId = result.getLong(1);
            statement.close();
            if (properties != null) {
                for (final String key : properties.stringPropertyNames()) {
                    final PreparedStatement ms = connection.prepareStatement(insertProperty(), Statement.RETURN_GENERATED_KEYS);
                    ms.setString(1, key);
                    ms.setString(2, properties.getProperty(key));
                    if (ms.executeUpdate() == 0) {
                        throw new IllegalStateException(); //TODO
                    }
                    final ResultSet mr = ms.getGeneratedKeys();
                    if (!mr.next()) {
                        throw new IllegalStateException(); //TODO
                    }
                    final long propertyId = mr.getLong(1);
                    ms.close();

                    final PreparedStatement sms = connection.prepareStatement(insertPropertyToPartitionExecution());
                    sms.setLong(1, partitionExecutionId);
                    sms.setLong(2, propertyId);
                    if (sms.executeUpdate() == 0) {
                        throw new IllegalStateException(); //TODO
                    }
                    sms.close();
                }
            }
            for (final Metric.MetricType type : Metric.MetricType.values()) {
                final PreparedStatement ms = connection.prepareStatement(insertMetric(), Statement.RETURN_GENERATED_KEYS);
                ms.setString(1, type.name());
                ms.setLong(2, 0);
                if (ms.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                final ResultSet mr = ms.getGeneratedKeys();
                if (!mr.next()) {
                    throw new IllegalStateException(); //TODO
                }
                final long metricId = mr.getLong(1);
                ms.close();

                final PreparedStatement sms = connection.prepareStatement(insertMetricToPartitionExecution());
                sms.setLong(1, partitionExecutionId);
                sms.setLong(2, metricId);
                sms.setString(3, type.name());
                if (sms.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                sms.close();
            }
            connection.commit();
            return new PartitionExecutionImpl.Builder()
                    .setPartitionExecutionId(partitionExecutionId)
                    .setStepExecutionId(stepExecutionId)
                    .setPartitionId(partitionId)
                    .setBatchStatus(BatchStatus.STARTING)
                    .setCreatedTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setPartitionProperties(properties)
                    .setPersistentUserData(_read(bytesPersistentUserData))
                    .setReaderCheckpoint(_read(bytesReaderCheckpoint))
                    .setWriterCheckpoint(_read(bytesWriterCheckpoint))
                    .setMetrics(MetricImpl.empty())
                    .build();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(updateStartJobExecution());
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setString(1, BatchStatus.STARTED.name());
            statement.setTimestamp(2, ts);
            statement.setTimestamp(3, ts);
            statement.setLong(4, jobExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            statement.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(updateUpdateJobExecution());
            statement.setString(1, batchStatus.name());
            statement.setTimestamp(2, new Timestamp(timestamp.getTime()));
            statement.setLong(3, jobExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            statement.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(updateFinishJobExecution());
            statement.setString(1, batchStatus.name());
            statement.setString(2, exitStatus);
            statement.setString(3, restartElementId);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(4, ts);
            statement.setTimestamp(5, ts);
            statement.setLong(6, jobExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            statement.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(queryJobExecutionHistory());
            statement.setLong(1, restartJobExecutionId);
            final ResultSet result = statement.executeQuery();
            final TLongList ids = new TLongArrayList();
            while (result.next()) {
                ids.add(result.getLong(1));
            }
            statement.close();
            final PreparedStatement main = connection.prepareStatement(insertJobExecutionHistory());
            main.setLong(1, jobExecutionId);
            main.setLong(2, restartJobExecutionId);
            if (main.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            main.close();
            for (final TLongIterator it = ids.iterator(); it.hasNext(); ) {
                final PreparedStatement ps = connection.prepareStatement(insertJobExecutionHistory());
                ps.setLong(1, jobExecutionId);
                ps.setLong(2, it.next());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                ps.close();
            }
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(updateStartStepExecution());
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setString(1, BatchStatus.STARTED.name());
            statement.setTimestamp(2, ts);
            statement.setTimestamp(3, ts);
            statement.setLong(4, stepExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            statement.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            final byte[] bytesPersistentUserDate = _bytes(persistentUserData);
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(updateUpdateStepExecution());
            this.setLargeObject(statement, 1, bytesPersistentUserDate);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(2, ts);
            statement.setLong(3, stepExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            statement.close();
            for (final Metric metric : metrics) {
                final PreparedStatement ps = connection.prepareStatement(updateStepMetric());
                ps.setLong(1, metric.getValue());
                ps.setLong(2, stepExecutionId);
                ps.setString(3, metric.getType().name());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                ps.close();
            }
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            final byte[] bytesPersistentUserDate = _bytes(persistentUserData);
            final byte[] bytesReaderCheckpoint = _bytes(readerCheckpoint);
            final byte[] bytesWriterCheckpoint = _bytes(writerCheckpoint);
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(updateUpdateStepExecutionWithCheckpoint());
            this.setLargeObject(statement, 1, bytesPersistentUserDate);
            this.setLargeObject(statement, 2, bytesReaderCheckpoint);
            this.setLargeObject(statement, 3, bytesWriterCheckpoint);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(4, ts);
            statement.setLong(5, stepExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException(); //TODO
            }
            statement.close();
            for (final Metric metric : metrics) {
                final PreparedStatement ps = connection.prepareStatement(updateStepMetric());
                ps.setLong(1, metric.getValue());
                ps.setLong(2, stepExecutionId);
                ps.setString(3, metric.getType().name());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                ps.close();
            }
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(updateFinishStepExecution());
            statement.setString(1, batchStatus.name());
            statement.setString(2, exitStatus);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(3, ts);
            statement.setTimestamp(4, ts);
            statement.setLong(5, stepExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            statement.close();
            for (final Metric metric : metrics) {
                final PreparedStatement ps = connection.prepareStatement(updateStepMetric());
                ps.setLong(1, metric.getValue());
                ps.setLong(2, stepExecutionId);
                ps.setString(3, metric.getType().name());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                ps.close();
            }
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(updateStartPartitionExecution());
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setString(1, BatchStatus.STARTED.name());
            statement.setTimestamp(2, ts);
            statement.setTimestamp(3, ts);
            statement.setLong(4, partitionExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            statement.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            final byte[] bytesPersistentUserDate = _bytes(persistentUserData);
            final byte[] bytesReaderCheckpoint = _bytes(readerCheckpoint);
            final byte[] bytesWriterCheckpoint = _bytes(writerCheckpoint);
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(updateUpdatePartitionExecution());
            this.setLargeObject(statement, 1, bytesPersistentUserDate);
            this.setLargeObject(statement, 2, bytesReaderCheckpoint);
            this.setLargeObject(statement, 3, bytesWriterCheckpoint);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(4, ts);
            statement.setLong(5, partitionExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            statement.close();
            for (final Metric metric : metrics) {
                final PreparedStatement ps = connection.prepareStatement(updatePartitionMetric());
                ps.setLong(1, metric.getValue());
                ps.setLong(2, partitionExecutionId);
                ps.setString(3, metric.getType().name());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                ps.close();
            }
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        Connection connection = null;
        try {
            final byte[] bytesPersistentUserDate = _bytes(persistentUserData);
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(updateFinishPartitionExecution());
            this.setLargeObject(statement, 1, bytesPersistentUserDate);
            statement.setString(2, batchStatus.name());
            statement.setString(3, exitStatus);
            final Timestamp ts = new Timestamp(timestamp.getTime());
            statement.setTimestamp(4, ts);
            statement.setTimestamp(5, ts);
            statement.setLong(6, partitionExecutionId);
            if (statement.executeUpdate() == 0) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            statement.close();
            for (final Metric metric : metrics) {
                final PreparedStatement ps = connection.prepareStatement(updatePartitionMetric());
                ps.setLong(1, metric.getValue());
                ps.setLong(2, partitionExecutionId);
                ps.setString(3, metric.getType().name());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(); //TODO
                }
                ps.close();
            }
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobNames());
            final ResultSet result = statement.executeQuery();
            final THashSet<String> names = new THashSet<String>();
            while (result.next()) {
                names.add(result.getString(1));
            }
            statement.close();
            return names;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobInstanceCount());
            statement.setString(1, jobName);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                throw new IllegalStateException(); //TODO
            }
            final int count = result.getInt(1);
            statement.close();
            if (count == 0) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            return count;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobInstances());
            statement.setString(1, jobName);
            final ResultSet result = statement.executeQuery();
            for (int i = 0; i < start; ++i) {
                if (!result.next()) {
                    statement.close();
                    throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
                }
            }
            if (!result.next()) {
                if (!result.next()) {
                    statement.close();
                    throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
                }
                statement.close();
                return Collections.emptyList();
            }
            final List<JobInstance> ret = new ArrayList<JobInstance>();
            for (int i = 0; i < count; ++i) {
                ret.add(_ji(result));
                if (!result.next()) {
                    break;
                }
            }
            statement.close();
            return ret;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryRunningExecutions());
            statement.setString(1, jobName);
            final ResultSet result = statement.executeQuery();
            final List<Long> ids = new ArrayList<Long>();
            while (result.next()) {
                ids.add(result.getLong(1));
            }
            if (ids.isEmpty()) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            statement.close();
            return ids;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(queryJobExecutionParameters());
            statement.setLong(1, jobExecutionId);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                final PreparedStatement ps = connection.prepareStatement(queryJobExecution());
                ps.setLong(1, jobExecutionId);
                final ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    statement.close();
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
                }
                ps.close();
                statement.close();
                connection.commit();
                return null;
            }
            final Properties properties = new Properties();
            do {
                properties.put(result.getString(1), result.getString(2));
            } while (result.next());
            connection.commit();
            statement.close();
            return properties;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public JobInstanceImpl getJobInstance(final long jobInstanceId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobInstance());
            statement.setLong(1, jobInstanceId);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
            }
            final JobInstanceImpl ret = _ji(result);
            statement.close();
            return ret;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public JobInstanceImpl getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobInstanceForJobExecution());
            statement.setLong(1, jobExecutionId);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final JobInstanceImpl ret = _ji(result);
            statement.close();
            return ret;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public List<JobExecutionImpl> getJobExecutions(final long jobInstanceId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobExecutionsForJobInstance());
            statement.setLong(1, jobInstanceId);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
            }
            final List<JobExecutionImpl> ret = new ArrayList<JobExecutionImpl>();
            do {
                ret.add(_je(connection, result));
            } while (result.next());
            statement.close();
            return ret;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public JobExecutionImpl getJobExecution(final long jobExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryJobExecution());
            statement.setLong(1, jobExecutionId);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final JobExecutionImpl ret = _je(connection, result);
            statement.close();
            return ret;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public JobExecutionImpl restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement is = connection.prepareStatement(queryJobInstanceForJobExecution());
            is.setLong(1, jobExecutionId);
            final ResultSet ir = is.executeQuery();
            if (!ir.next()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final JobInstanceImpl jobInstance = _ji(ir);
            final PreparedStatement ls = connection.prepareStatement(queryLatestJobExecution());
            final ResultSet lr = ls.executeQuery();
            if (!lr.next()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final JobExecutionImpl latest = _je(connection, lr);
            if (jobExecutionId != latest.getExecutionId()) {
                throw new JobExecutionNotMostRecentException(Messages.format("CHAINLINK-006004.repository.not.most.recent.execution", jobExecutionId, jobInstance.getInstanceId()));
            }
            switch (latest.getBatchStatus()) {
                case STOPPED:
                case FAILED:
                    break;
                case COMPLETED:
                    throw new JobExecutionAlreadyCompleteException(Messages.format("CHAINLINK-006006.execution.repository.execution.already.complete", jobExecutionId));
                default:
                    throw new JobRestartException(Messages.format("CHAINLINK-006007.execution.repository.execution.not.eligible.for.restart", latest.getExecutionId(), BatchStatus.STOPPED, BatchStatus.FAILED, latest.getBatchStatus()));
            }
            final JobExecutionImpl jobExecution =  _createJobExecution(connection, jobInstance, parameters, new Date());
            connection.commit();
            return jobExecution;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public List<StepExecutionImpl> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(queryStepExecutionsForJobExecution());
            statement.setLong(1, jobExecutionId);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                final PreparedStatement ps = connection.prepareStatement(queryJobExecution());
                ps.setLong(1, jobExecutionId);
                final ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    statement.close();
                    connection.commit();
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
                }
                statement.close();
                connection.commit();
                return Collections.emptyList();
            }
            final ArrayList<StepExecutionImpl> ret = new ArrayList<StepExecutionImpl>();
            do {
                ret.add(_se(connection, result));
            } while (result.next());
            statement.close();
            connection.commit();
            return ret;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public StepExecutionImpl getStepExecution(final long stepExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final StepExecutionImpl stepExecution = _getStepExecution(connection, stepExecutionId);
            connection.commit();
            return stepExecution;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private StepExecutionImpl _getStepExecution(final Connection connection, final long stepExecutionId) throws Exception {
        final PreparedStatement statement = connection.prepareStatement(queryStepExecution());
        statement.setLong(1, stepExecutionId);
        final ResultSet result = statement.executeQuery();
        if(!result.next()) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        final StepExecutionImpl stepExecution = _se(connection, result);
        statement.close();
        return stepExecution;
    }

    @Override
    public StepExecutionImpl getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(queryPreviousStepExecution());
            statement.setLong(1, jobExecutionId);
            statement.setLong(2, jobExecutionId);
            statement.setLong(3, stepExecutionId);
            statement.setLong(4, stepExecutionId);
            statement.setString(5, stepName);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                statement.close();
                connection.commit();
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
            }
            final StepExecutionImpl stepExecution = _se(connection, result);
            statement.close();
            connection.commit();
            return stepExecution;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public StepExecutionImpl getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(queryLatestStepExecution());
            statement.setLong(1, jobExecutionId);
            statement.setLong(2, jobExecutionId);
            statement.setString(3, stepName);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                statement.close();
                connection.commit();
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
            }
            final StepExecutionImpl stepExecution = _se(connection, result);
            statement.close();
            connection.commit();
            return stepExecution;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            final PreparedStatement statement = connection.prepareStatement(queryStepExecutionCount());
            statement.setLong(1, jobExecutionId);
            statement.setString(2, stepName);
            final ResultSet result = statement.executeQuery();
            if (!result.next()) {
                //TODO
            }
            final int count = result.getInt(1);
            statement.close();
            return count;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final StepExecution[] stepExecutions = new StepExecution[stepExecutionIds.length];
            for (int i = 0; i < stepExecutionIds.length; ++i) {
                stepExecutions[i] = _getStepExecution(connection, stepExecutionIds[i]);
            }
            connection.commit();
            return stepExecutions;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(queryUnfinishedPartitionExecutions());
            statement.setLong(1, stepExecutionId);
            statement.setString(2, BatchStatus.FAILED.name());
            statement.setString(3, BatchStatus.STOPPED.name());
            statement.setString(4, BatchStatus.STOPPING.name());
            statement.setString(5, BatchStatus.STARTED.name());
            statement.setString(6, BatchStatus.STARTING.name());
            final ResultSet result = statement.executeQuery();
            final ArrayList<PartitionExecution> ret = new ArrayList<PartitionExecution>();
            while (result.next()) {
                ret.add(_pe(connection, result));
            }
            statement.close();
            connection.commit();
            return ret.toArray(new PartitionExecution[ret.size()]);
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public PartitionExecutionImpl getPartitionExecution(final long partitionExecutionId) throws Exception {
        Connection connection = null;
        try {
            connection = _connection();
            connection.setAutoCommit(false);
            final PartitionExecutionImpl partitionExecution = _getPartitionExecution(connection, partitionExecutionId);
            connection.commit();
            return partitionExecution;
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    private PartitionExecutionImpl _getPartitionExecution(final Connection connection, final long partitionExecutionId) throws Exception {
        final PreparedStatement statement = connection.prepareStatement(queryPartitionExecution());
        statement.setLong(1, partitionExecutionId);
        final ResultSet result = statement.executeQuery();
        if(!result.next()) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
        }
        final PartitionExecutionImpl partitionExecution = _pe(connection, result);
        statement.close();
        return partitionExecution;
    }

    protected String insertJobInstance() {
        return "insert into job_instance (job_name, jsl_name, create_time) values (?, ?, ?);";
    }

    protected String insertJobExecution() {
        return "insert into job_execution (job_instance_id, job_name, batch_status, create_time, updated_time) values (?, ?, ?, ?, ?);";
    }

    protected String insertStepExecution() {
        return "insert into step_execution (job_execution_id, step_name, batch_status, create_time, updated_time) values (?, ?, ?, ?, ?);";
    }

    protected String insertPartitionExecution() {
        return "insert into partition_execution (step_execution_id, partition_id, batch_status, persistent_user_data, reader_checkpoint, writer_checkpoint, create_time, updated_time) values (?, ?, ?, ?, ?, ?, ?, ?);";
    }

    protected String insertProperty() {
        return "insert into property (name, value) values (?, ?);";
    }

    protected String insertPropertyToJobExecution() {
        return "insert into job_execution_property (job_execution_id, property_id) values (?, ?);";
    }

    protected String insertPropertyToPartitionExecution() {
        return "insert into partition_execution_property (partition_execution_id, property_id) values (?, ?);";
    }

    protected String insertMetric() {
        return "insert into metric (type, value) values (?, ?);";
    }

    protected String insertMetricToStepExecution() {
        return "insert into step_execution_metric (step_execution_id, metric_id, metric_type) values (?, ?, ?);";
    }

    protected String insertMetricToPartitionExecution() {
        return "insert into partition_execution_metric (partition_execution_id, metric_id, metric_type) values (?, ?, ?);";
    }

    protected String insertJobExecutionHistory() {
        return "insert into job_execution_history (job_execution_id, previous_job_execution_id) values (?, ?);";
    }

    protected String updateStartJobExecution() {
        return "update job_execution set batch_status = ?, start_time = ?, updated_time = ? where id = ?;";
    }

    protected String updateUpdateJobExecution() {
        return "update job_execution set batch_status = ?, updated_time = ? where id = ?;";
    }

    protected String updateFinishJobExecution() {
        return "update job_execution set batch_status = ?, exit_status = ?, restart_element_id = ?, updated_time = ?, end_time = ? where id = ?;";
    }

    protected String updateStartStepExecution() {
        return "update step_execution set batch_status = ?, start_time = ?, updated_time = ? where id = ?;";
    }

    protected String updateUpdateStepExecution() {
        return "update step_execution set persistent_user_data = ?, updated_time = ? where id = ?;";
    }

    protected String updateUpdateStepExecutionWithCheckpoint() {
        return "update step_execution set persistent_user_data = ?, reader_checkpoint = ?, writer_checkpoint = ?, updated_time = ? where id = ?;";
    }

    protected String updateFinishStepExecution() {
        return "update step_execution set batch_status = ?, exit_status = ?, updated_time = ?, end_time = ? where id = ?;";
    }

    protected String updateStepMetric() {
        return "update metric m set value = ? where m.id in (select e.metric_id from step_execution_metric e where e.step_execution_id = ?) and m.type = ?;";
    }

    protected String updateStartPartitionExecution() {
        return "update partition_execution set batch_status = ?, start_time = ?, updated_time = ? where id = ?;";
    }

    protected String updateUpdatePartitionExecution() {
        return "update partition_execution set persistent_user_data = ?, reader_checkpoint = ?, writer_checkpoint = ?, updated_time = ? where id = ?;";
    }

    protected String updateFinishPartitionExecution() {
        return "update partition_execution set persistent_user_data = ?, batch_status = ?, exit_status = ?, updated_time = ?, end_time = ? where id = ?;";
    }

    protected String updatePartitionMetric() {
        return "update metric m set value = ? where m.id in (select e.metric_id from partition_execution_metric e where e.partition_execution_id = ?) and m.type = ?;";
    }

    protected String queryJobNames() {
        return "select distinct i.job_name from job_instance i order by i.job_name asc;";
    }

    protected String queryJobInstanceCount() {
        return "select count(i.*) from job_instance i where i.job_name = ?;";
    }

    protected String queryJobInstances() {
        return "select i.id, i.job_name, i.jsl_name, i.create_time from job_instance i where i.job_name = ? order by i.create_time desc;";
    }

    protected String queryRunningExecutions() {
        return "select j.id from job_execution j where j.job_name = ? order by j.create_time desc;";
    }

    protected String queryJobExecutionParameters() {
        return "select p.name, p.value from property p join job_execution_property j on p.id=j.property_id and j.job_execution_id = ?;";
    }

    protected String queryJobExecution() {
        return "select j.id, j.job_instance_id, j.job_name, j.batch_status, j.exit_status, j.create_time, j.start_time, j.updated_time, j.end_time, j.restart_element_id from job_execution j where j.id = ?;";
    }

    protected String queryLatestJobExecution() {
        return "select j.id, j.job_instance_id, j.job_name, j.batch_status, j.exit_status, j.create_time, j.start_time, j.updated_time, j.end_time, j.restart_element_id from job_execution j order by j.create_time desc limit 1;";
    }

    protected String queryJobExecutionHistory() {
        return "select h.previous_job_execution_id from job_execution_history h where h.job_execution_id = ?;";
    }

    protected String queryJobInstance() {
        return "select i.id, i.job_name, i.jsl_name, i.create_time from job_instance i where i.id = ?;";
    }

    protected String queryJobInstanceForJobExecution() {
        return "select i.id, i.job_name, i.jsl_name, i.create_time from job_instance i join job_execution j on i.id=j.job_instance_id and j.id = ?;";
    }

    protected String queryJobExecutionsForJobInstance() {
        return "select j.id, j.job_instance_id, j.job_name, j.batch_status, j.exit_status, j.create_time, j.start_time, j.updated_time, j.end_time, j.restart_element_id from job_execution j where j.job_instance_id = ?;";
    }

    protected String queryStepExecutionsForJobExecution() {
        return "select s.id, s.job_execution_id, s.step_name, s.batch_status, s.exit_status, s.create_time, s.start_time, s.updated_time, s.end_time, s.persistent_user_data, s.reader_checkpoint, s.writer_checkpoint from step_execution s where s.job_execution_id = ?;";
    }

    protected String queryStepExecution() {
        return "select s.id, s.job_execution_id, s.step_name, s.batch_status, s.exit_status, s.create_time, s.start_time, s.updated_time, s.end_time, s.persistent_user_data, s.reader_checkpoint, s.writer_checkpoint from step_execution s where s.id = ?;";
    }

    protected String queryStepMetric() {
        return "select m.type, m.value from metric m join step_execution_metric e on m.id=e.metric_id and e.step_execution_id = ?;";
    }

    protected String queryPreviousStepExecution() {
        return "select s.id, s.job_execution_id, s.step_name, s.batch_status, s.exit_status, s.create_time, s.start_time, s.updated_time, s.end_time, s.persistent_user_data, s.reader_checkpoint, s.writer_checkpoint" +
                " from step_execution s join job_execution j on s.job_execution_id = j.id where" +
                " (j.id = ? or j.id in" +
                " (select p.id from job_execution_history h join job_execution p on h.previous_job_execution_id = p.id where h.job_execution_id = ?)" +
                " ) and s.id <> ? and s.create_time < (select t.create_time from step_execution t where t.id = ?)" +
                " and s.step_name = ? order by s.create_time desc;";
    }

    protected String queryLatestStepExecution() {
        return "select s.id, s.job_execution_id, s.step_name, s.batch_status, s.exit_status, s.create_time, s.start_time, s.updated_time, s.end_time, s.persistent_user_data, s.reader_checkpoint, s.writer_checkpoint" +
                " from step_execution s join job_execution j on s.job_execution_id = j.id where" +
                " (j.id = ? or j.id in" +
                " (select p.id from job_execution_history h join job_execution p on h.previous_job_execution_id = p.id where h.job_execution_id = ?)" +
                " ) and s.step_name = ? order by s.create_time desc;";
    }

    protected String queryStepExecutionCount() {
        return "select count(*) from (select distinct s.id from step_execution s join job_execution_history h on (s.job_execution_id=h.previous_job_execution_id and h.job_execution_id = ?) and s.step_name = ?) as foo;";
    }

    protected String queryUnfinishedPartitionExecutions() {
        return "select p.id, p.step_execution_id, p.partition_id, p.batch_status, p.exit_status, p.create_time, p.start_time, p.updated_time, p.end_time, p.persistent_user_data, p.reader_checkpoint, p.writer_checkpoint from partition_execution p where p.step_execution_id = ? and (p.batch_status = ? or p.batch_status = ? or p.batch_status = ? or p.batch_status = ? or p.batch_status = ?);";
    }

    protected String queryPartitionExecution() {
        return "select p.id, p.step_execution_id, p.partition_id, p.batch_status, p.exit_status, p.create_time, p.start_time, p.updated_time, p.end_time, p.persistent_user_data, p.reader_checkpoint, p.writer_checkpoint from partition_execution p where p.id = ?;";
    }

    protected String queryPartitionMetric() {
        return "select m.type, m.value from metric m join partition_execution_metric e on m.id=e.metric_id and e.partition_execution_id = ?;";
    }

    protected String queryPartitionParameters() {
        return  "select p.name, p.value from property p join partition_execution_property e on p.id=e.property_id and e.partition_execution_id = ?;";
    }

    protected void setLargeObject(final PreparedStatement statement, final int index, final byte[] bytes) throws SQLException {
        statement.setBytes(index, bytes);
    }

    protected Serializable getLargeObject(final ResultSet result, final int index) throws Exception {
        return _read(result.getBinaryStream(index));
    }

    private PartitionExecutionImpl _pe(final Connection connection, final ResultSet result) throws Exception {
        final long partitionExecutionId = result.getLong(1);
        final PartitionExecutionImpl.Builder builder = new PartitionExecutionImpl.Builder()
                .setPartitionExecutionId(partitionExecutionId)
                .setStepExecutionId(result.getLong(2))
                .setPartitionId(result.getInt(3))
                .setBatchStatus(BatchStatus.valueOf(result.getString(4)))
                .setExitStatus(result.getString(5))
                .setCreatedTime(result.getTimestamp(6))
                .setStartTime(result.getTimestamp(7))
                .setUpdatedTime(result.getTimestamp(8))
                .setEndTime(result.getTimestamp(9))
                .setPersistentUserData(getLargeObject(result, 10))
                .setReaderCheckpoint(getLargeObject(result, 11))
                .setWriterCheckpoint(getLargeObject(result, 12));

        final PreparedStatement ms = connection.prepareStatement(queryPartitionMetric());
        ms.setLong(1, partitionExecutionId);
        final ResultSet mr = ms.executeQuery();
        final ArrayList<Metric> metrics = new ArrayList<Metric>(Metric.MetricType.values().length);
        while (mr.next()) {
            metrics.add(new MetricImpl(
                    Metric.MetricType.valueOf(mr.getString(1)),
                    mr.getLong(2)
            ));
        }
        ms.close();
        builder.setMetrics(metrics.toArray(new Metric[metrics.size()]));

        final PreparedStatement ps = connection.prepareStatement(queryPartitionParameters());
        ps.setLong(1, partitionExecutionId);
        final ResultSet pr = ps.executeQuery();
        final Properties properties = new Properties();
        while (pr.next()) {
            properties.put(
                    pr.getString(1),
                    pr.getString(2)
            );
        }
        ps.close();
        builder.setPartitionProperties(properties);

        return builder.build();
    }

    private StepExecutionImpl _se(final Connection connection, final ResultSet result) throws Exception {
        final long stepExecutionId = result.getLong(1);
        final StepExecutionImpl.Builder builder = new StepExecutionImpl.Builder()
                .setStepExecutionId(stepExecutionId)
                .setJobExecutionId(result.getLong(2))
                .setStepName(result.getString(3))
                .setBatchStatus(BatchStatus.valueOf(result.getString(4)))
                .setExitStatus(result.getString(5))
                .setCreatedTime(result.getTimestamp(6))
                .setStartTime(result.getTimestamp(7))
                .setUpdatedTime(result.getTimestamp(8))
                .setEndTime(result.getTimestamp(9))
                .setPersistentUserData(getLargeObject(result, 10))
                .setReaderCheckpoint(getLargeObject(result, 11))
                .setWriterCheckpoint(getLargeObject(result, 12));

        final PreparedStatement ms = connection.prepareStatement(queryStepMetric());
        ms.setLong(1, stepExecutionId);
        final ResultSet mr = ms.executeQuery();
        final ArrayList<Metric> metrics = new ArrayList<Metric>(Metric.MetricType.values().length);
        while (mr.next()) {
            metrics.add(new MetricImpl(
                    Metric.MetricType.valueOf(mr.getString(1)),
                    mr.getLong(2)
            ));
        }
        ms.close();
        builder.setMetrics(metrics.toArray(new Metric[metrics.size()]));
        return builder.build();
    }

    private JobExecutionImpl _je(final Connection connection, final ResultSet result) throws SQLException {
        final long jobExecutionId = result.getLong(1);
        final JobExecutionImpl.Builder builder = new JobExecutionImpl.Builder()
            .setJobExecutionId(jobExecutionId)
            .setJobInstanceId(result.getLong(2))
            .setJobName(result.getString(3))
            .setBatchStatus(BatchStatus.valueOf(result.getString(4)))
            .setExitStatus(result.getString(5))
            .setCreatedTime(result.getTimestamp(6))
            .setStartTime(result.getTimestamp(7))
            .setUpdatedTime(result.getTimestamp(8))
            .setEndTime(result.getTimestamp(9))
            .setRestartElementId(result.getString(10));

        final PreparedStatement ps = connection.prepareStatement(queryJobExecutionParameters());
        ps.setLong(1, jobExecutionId);
        final ResultSet pr = ps.executeQuery();
        final Properties properties = new Properties();
        while (pr.next()) {
            properties.put(
                    pr.getString(1),
                    pr.getString(2)
            );
        }
        ps.close();
        builder.setParameters(properties);

        return builder.build();
    }

    private JobInstanceImpl _ji(final ResultSet result) throws SQLException {
        return new JobInstanceImpl.Builder()
                .setInstanceId(result.getLong(1))
                .setJobName(result.getString(2))
                .setJslName(result.getString(3))
                .setCreatedTime(result.getTimestamp(4))
                .build();
    }

    //TODO These need to be moved to marshalling
    private byte[] _bytes(final Serializable that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(that);
        oos.flush();
        return baos.toByteArray();
    }

    protected Serializable _read(final byte[] that) throws ClassNotFoundException, IOException {
        return that == null ? null : (Serializable) new ObjectInputStream(new ByteArrayInputStream(that)).readObject();
    }

    protected Serializable _read(final InputStream that) throws ClassNotFoundException, IOException, SQLException {
        return that == null ? null : (Serializable) new ObjectInputStream(that).readObject();
    }
}
