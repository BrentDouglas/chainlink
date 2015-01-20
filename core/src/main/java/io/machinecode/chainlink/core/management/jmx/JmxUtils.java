package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.core.repository.JobExecutionImpl;
import io.machinecode.chainlink.core.repository.JobInstanceImpl;
import io.machinecode.chainlink.core.repository.MetricImpl;
import io.machinecode.chainlink.core.repository.StepExecutionImpl;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JmxUtils {

    static final String[] PROPERTY_ATTRIBUTES;
    static final CompositeType PROPERTY;
    static final TabularType PROPERTIES;
    static final String[] JOB_INSTANCE_ATTRIBUTES;
    static final CompositeType JOB_INSTANCE;
    static final String[] JOB_EXECUTION_ATTRIBUTES;
    static final CompositeType JOB_EXECUTION;
    static final String[] STEP_EXECUTION_ATTRIBUTES;
    static final CompositeType STEP_EXECUTION;
    static final TabularType LIST_JOB_INSTANCE;
    static final TabularType LIST_JOB_EXECUTION;
    static final TabularType LIST_STEP_EXECUTION;

    static {
        try {
            {
                PROPERTY_ATTRIBUTES = new String[]{"key", "value"};
                final OpenType[] types = { SimpleType.STRING, SimpleType.STRING };
                PROPERTY = new CompositeType("Property", "Property", PROPERTY_ATTRIBUTES, PROPERTY_ATTRIBUTES, types);
                PROPERTIES = new TabularType("Properties", "Properties", PROPERTY, PROPERTY_ATTRIBUTES);
            }
            {
                JOB_INSTANCE_ATTRIBUTES = new String[]{"jobInstanceId", "jobName", "jslName", "createTime"};
                final OpenType[] types = { SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.DATE };
                JOB_INSTANCE = new CompositeType("ExtendedJobInstance", "ExtendedJobInstance", JOB_INSTANCE_ATTRIBUTES, JOB_INSTANCE_ATTRIBUTES, types);
                LIST_JOB_INSTANCE = new TabularType("List<ExtendedJobInstance>", "List<ExtendedJobInstance>", JOB_INSTANCE, JOB_INSTANCE_ATTRIBUTES);
            }
            {
                JOB_EXECUTION_ATTRIBUTES = new String[]{
                        "jobExecutionId", "jobName", "batchStatus", "createTime",
                        "startTime", "endTime", "lastUpdatedTime", "exitStatus",
                        "restartElementId", "jobInstanceId", "parameters"
                };
                final OpenType[] types = {
                        SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.DATE,
                        SimpleType.DATE, SimpleType.DATE, SimpleType.DATE, SimpleType.STRING,
                        SimpleType.STRING, SimpleType.LONG, PROPERTIES
                };
                JOB_EXECUTION = new CompositeType("ExtendedJobExecution", "ExtendedJobExecution", JOB_EXECUTION_ATTRIBUTES, JOB_EXECUTION_ATTRIBUTES, types);
                LIST_JOB_EXECUTION = new TabularType("List<ExtendedJobExecution>", "List<ExtendedJobExecution>", JOB_EXECUTION, JOB_EXECUTION_ATTRIBUTES);
            }
            {
                final ArrayType<byte[]> serializable = ArrayType.getPrimitiveArrayType(byte[].class);
                STEP_EXECUTION_ATTRIBUTES = new String[]{
                        "jobExecutionId", "stepExecutionId", "stepName", "batchStatus",
                        "createTime", "startTime", "endTime", "lastUpdatedTime",
                        "exitStatus", "persistentUserData", "readerCheckpoint", "writerCheckpoint",
                        "readCount", "writeCount", "commitCount", "rollbackCount",
                        "readSkipCount", "processSkipCount", "filterCount", "writeSkipCount"
                };
                final OpenType[] types = {
                        SimpleType.LONG, SimpleType.LONG, SimpleType.STRING, SimpleType.STRING,
                        SimpleType.DATE, SimpleType.DATE, SimpleType.DATE, SimpleType.DATE,
                        SimpleType.STRING, serializable, serializable, serializable,
                        SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG,
                        SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG
                };
                STEP_EXECUTION = new CompositeType("ExtendedStepExecution", "ExtendedStepExecution", STEP_EXECUTION_ATTRIBUTES, STEP_EXECUTION_ATTRIBUTES, types);
                LIST_STEP_EXECUTION = new TabularType("List<ExtendedStepExecution>", "List<ExtendedStepExecution>", STEP_EXECUTION, STEP_EXECUTION_ATTRIBUTES);
            }
        } catch (final OpenDataException e) {
            throw new RuntimeException(e);
        }
    }

    static Properties readProperties(final TabularData data) throws OpenDataException {
        final Properties properties = new Properties();
        if (data == null) {
            return properties;
        }
        for (final Object o : data.values()) {
            final CompositeData cd = (CompositeData)o;
            properties.setProperty(
                    (String) cd.get("key"),
                    (String) cd.get("value")
            );
        }
        return properties;
    }

    static List<JobInstance> readJobInstances(final TabularData data) throws OpenDataException {
        if (data == null) {
            return Collections.emptyList();
        }
        final ArrayList<JobInstance> list = new ArrayList<>(data.size());
        for (final Object o :data.values()) {
            final CompositeData cd = (CompositeData)o;
            list.add(readExtendedJobInstance(cd));
        }
        return list;
    }

    static List<JobExecution> readJobExecutions(final TabularData data) throws OpenDataException {
        if (data == null) {
            return Collections.emptyList();
        }
        final ArrayList<JobExecution> list = new ArrayList<>(data.size());
        for (final Object o :data.values()) {
            final CompositeData cd = (CompositeData)o;
            list.add(readExtendedJobExecution(cd));
        }
        return list;
    }

    static List<StepExecution> readStepExecutions(final TabularData data, final Marshalling marshalling, final ClassLoader loader) throws OpenDataException, IOException, ClassNotFoundException {
        if (data == null) {
            return Collections.emptyList();
        }
        final ArrayList<StepExecution> list = new ArrayList<>(data.size());
        for (final Object o :data.values()) {
            final CompositeData cd = (CompositeData)o;
            list.add(readExtendedStepExecution(cd, marshalling, loader));
        }
        return list;
    }

    static ExtendedJobInstance readExtendedJobInstance(final CompositeData data) throws OpenDataException {
        if (data == null) {
            return null;
        }
        return new JobInstanceImpl.Builder()
                .setJobInstanceId((long)data.get("jobInstanceId"))
                .setJobName((String) data.get("jobName"))
                .setJslName((String) data.get("jslName"))
                .setCreateTime((Date) data.get("createTime"))
                .build();
    }

    static ExtendedJobExecution readExtendedJobExecution(final CompositeData data) throws OpenDataException {
        if (data == null) {
            return null;
        }
        return new JobExecutionImpl.Builder()
                .setJobExecutionId((long) data.get("jobExecutionId"))
                .setJobName((String) data.get("jobName"))
                .setBatchStatus(BatchStatus.valueOf((String) data.get("batchStatus")))
                .setCreateTime((Date) data.get("createTime"))
                .setStartTime((Date) data.get("startTime"))
                .setEndTime((Date) data.get("endTime"))
                .setLastUpdatedTime((Date) data.get("lastUpdatedTime"))
                .setExitStatus((String) data.get("exitStatus"))
                .setRestartElementId((String) data.get("restartElementId"))
                .setJobInstanceId((long) data.get("jobInstanceId"))
                .setJobParameters(readProperties((TabularData) data.get("parameters")))
                .build();
    }

    static ExtendedStepExecution readExtendedStepExecution(final CompositeData data, final Marshalling marshalling, final ClassLoader loader) throws OpenDataException, IOException, ClassNotFoundException {
        if (data == null) {
            return null;
        }
        final Object jobExecutionId = data.get("jobExecutionId");
        return new StepExecutionImpl.Builder()
                .setJobExecutionId(jobExecutionId == null ? -1 : (long) jobExecutionId)
                .setStepExecutionId((long) data.get("stepExecutionId"))
                .setStepName((String) data.get("stepName"))
                .setBatchStatus(BatchStatus.valueOf((String) data.get("batchStatus")))
                .setCreateTime((Date) data.get("createTime"))
                .setStartTime((Date) data.get("startTime"))
                .setEndTime((Date) data.get("endTime"))
                .setUpdatedTime((Date) data.get("lastUpdatedTime"))
                .setExitStatus((String) data.get("exitStatus"))
                .setPersistentUserData(marshalling.unmarshall((byte[]) data.get("persistentUserData"), loader))
                .setReaderCheckpoint(marshalling.unmarshall((byte[]) data.get("readerCheckpoint"), loader))
                .setWriterCheckpoint(marshalling.unmarshall((byte[]) data.get("writerCheckpoint"), loader))
                .setMetrics(readMetrics(data))
                .build();
    }


    static TabularData writeProperties(final Properties parameters) throws OpenDataException {
        final TabularData data = new TabularDataSupport(PROPERTIES);
        for (final String key : parameters.stringPropertyNames()) {
            data.put(new CompositeDataSupport(PROPERTY, PROPERTY_ATTRIBUTES, new Object[]{key, parameters.getProperty(key)}));
        }
        return data;
    }

    static CompositeDataSupport writeJobInstance(final JobInstance jobInstance) throws OpenDataException {
        if (jobInstance instanceof ExtendedJobInstance) {
            return writeExtendedJobInstance((ExtendedJobInstance) jobInstance);
        }
        return new CompositeDataSupport(JOB_INSTANCE, JOB_INSTANCE_ATTRIBUTES, new Object[]{
                jobInstance.getInstanceId(),
                jobInstance.getJobName(),
                null,
                null
        });
    }

    static CompositeDataSupport writeExtendedJobInstance(final ExtendedJobInstance jobInstance) throws OpenDataException {
        return new CompositeDataSupport(JOB_INSTANCE, JOB_INSTANCE_ATTRIBUTES, new Object[]{
                jobInstance.getInstanceId(),
                jobInstance.getJobName(),
                jobInstance.getJslName(),
                jobInstance.getCreateTime() // jobInstance.getCreateTime() == null ? "-" : format.format(jobInstance.getCreateTime()));
        });
    }

    static CompositeDataSupport writeJobExecution(final JobExecution jobExecution) throws OpenDataException {
        if (jobExecution instanceof ExtendedJobExecution) {
            return writeExtendedJobExecution((ExtendedJobExecution) jobExecution);
        }
        return new CompositeDataSupport(JOB_EXECUTION, JOB_EXECUTION_ATTRIBUTES, new Object[]{
                jobExecution.getExecutionId(),
                jobExecution.getJobName(),
                jobExecution.getBatchStatus().toString(),
                jobExecution.getCreateTime(), // == null ? "-" : format.format(jobExecution.getCreateTime()));
                jobExecution.getStartTime(),// == null ? "-" : format.format(jobExecution.getStartTime()));
                jobExecution.getEndTime(),// == null ? "-" : format.format(jobExecution.getEndTime()));
                jobExecution.getLastUpdatedTime(),// == null ? "-" : format.format(jobExecution.getLastUpdatedTime()));
                jobExecution.getExitStatus(),
                null,
                null,
                writeProperties(jobExecution.getJobParameters())
        });
    }

    static CompositeDataSupport writeExtendedJobExecution(final ExtendedJobExecution jobExecution) throws OpenDataException {
        return new CompositeDataSupport(JOB_EXECUTION, JOB_EXECUTION_ATTRIBUTES, new Object[]{
                jobExecution.getExecutionId(),
                jobExecution.getJobName(),
                jobExecution.getBatchStatus().toString(),
                jobExecution.getCreateTime(), // == null ? "-" : format.format(jobExecution.getCreateTime()));
                jobExecution.getStartTime(),// == null ? "-" : format.format(jobExecution.getStartTime()));
                jobExecution.getEndTime(),// == null ? "-" : format.format(jobExecution.getEndTime()));
                jobExecution.getLastUpdatedTime(),// == null ? "-" : format.format(jobExecution.getLastUpdatedTime()));
                jobExecution.getExitStatus(),
                jobExecution.getRestartElementId(),
                jobExecution.getJobInstanceId(),
                writeProperties(jobExecution.getJobParameters())
        });
    }

    static CompositeDataSupport writeStepExecution(final StepExecution stepExecution, final Marshalling marshalling) throws OpenDataException, IOException {
        if (stepExecution instanceof ExtendedStepExecution) {
            return writeExtendedStepExecution((ExtendedStepExecution) stepExecution, marshalling);
        }
        final Object[] values = new Object[]{
                null,
                stepExecution.getStepExecutionId(),
                stepExecution.getStepName(),
                stepExecution.getBatchStatus().toString(),
                null,
                stepExecution.getStartTime(),// == null ? "-" : format.format(stepExecution.getStartTime()));
                stepExecution.getEndTime(),// == null ? "-" : format.format(stepExecution.getEndTime()));
                null,
                stepExecution.getExitStatus(),
                marshalling.marshall(stepExecution.getPersistentUserData()),
                null,
                null,
                -1, -1, -1, -1,
                -1, -1, -1, -1
        };
        writeMetrics(stepExecution.getMetrics(), values, 12);
        return new CompositeDataSupport(STEP_EXECUTION, STEP_EXECUTION_ATTRIBUTES, values);
    }

    static CompositeDataSupport writeExtendedStepExecution(final ExtendedStepExecution stepExecution, final Marshalling marshalling) throws OpenDataException, IOException {
        final Object[] values = new Object[]{
                stepExecution.getJobExecutionId(),
                stepExecution.getStepExecutionId(),
                stepExecution.getStepName(),
                stepExecution.getBatchStatus().toString(),
                stepExecution.getCreateTime(),
                stepExecution.getStartTime(),// == null ? "-" : format.format(stepExecution.getStartTime()));
                stepExecution.getEndTime(),// == null ? "-" : format.format(stepExecution.getEndTime()));
                stepExecution.getUpdatedTime(),
                stepExecution.getExitStatus(),
                marshalling.marshall(stepExecution.getPersistentUserData()),
                marshalling.marshall(stepExecution.getReaderCheckpoint()),
                marshalling.marshall(stepExecution.getWriterCheckpoint()),
                -1, -1, -1, -1,
                -1, -1, -1, -1
        };
        writeMetrics(stepExecution.getMetrics(), values, 12);
        return new CompositeDataSupport(STEP_EXECUTION, STEP_EXECUTION_ATTRIBUTES, values);
    }

    static Metric[] readMetrics(final CompositeData data) throws OpenDataException {
        final Metric[] metrics = new Metric[8];
        metrics[0] = new MetricImpl(Metric.MetricType.READ_COUNT, (long) data.get("readCount"));
        metrics[1] = new MetricImpl(Metric.MetricType.WRITE_COUNT, (long) data.get("writeCount"));
        metrics[2] = new MetricImpl(Metric.MetricType.COMMIT_COUNT, (long) data.get("commitCount"));
        metrics[3] = new MetricImpl(Metric.MetricType.ROLLBACK_COUNT, (long) data.get("rollbackCount"));
        metrics[4] = new MetricImpl(Metric.MetricType.READ_SKIP_COUNT, (long) data.get("readSkipCount"));
        metrics[5] = new MetricImpl(Metric.MetricType.PROCESS_SKIP_COUNT, (long) data.get("processSkipCount"));
        metrics[6] = new MetricImpl(Metric.MetricType.FILTER_COUNT, (long) data.get("filterCount"));
        metrics[7] = new MetricImpl(Metric.MetricType.WRITE_SKIP_COUNT, (long) data.get("writeSkipCount"));
        return metrics;
    }

    static void writeMetrics(final Metric[] metrics, final Object[] values, final int index) {
        for(final Metric metric : metrics) {
            switch (metric.getType()) {
                case READ_COUNT: values[index] = metric.getValue(); break;
                case WRITE_COUNT: values[index + 1] = metric.getValue(); break;
                case COMMIT_COUNT: values[index + 2] = metric.getValue(); break;
                case ROLLBACK_COUNT: values[index + 3] = metric.getValue(); break;
                case READ_SKIP_COUNT: values[index + 4] = metric.getValue(); break;
                case PROCESS_SKIP_COUNT: values[index + 5] = metric.getValue(); break;
                case FILTER_COUNT: values[index + 6] = metric.getValue(); break;
                case WRITE_SKIP_COUNT: values[index + 7] = metric.getValue(); break;
            }
        }
    }
}
