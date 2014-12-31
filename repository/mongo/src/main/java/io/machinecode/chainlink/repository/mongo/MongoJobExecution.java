package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.machinecode.chainlink.core.repository.JobExecutionImpl;
import org.bson.types.ObjectId;

import javax.batch.runtime.BatchStatus;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@JsonAutoDetect(
        getterVisibility = NONE,
        isGetterVisibility = NONE,
        setterVisibility = NONE,
        fieldVisibility = NONE,
        creatorVisibility = NONE
)
@JsonDeserialize(builder = MongoJobExecution.Builder.class)
public class MongoJobExecution extends JobExecutionImpl {
    private static final long serialVersionUID = 1L;

    private final ObjectId _id;
    private final List<Long> previousJobExecutionIds;

    public MongoJobExecution(final _Builder<?> builder) {
        super(builder);
        this._id = builder._id;
        this.previousJobExecutionIds = builder.previousJobExecutionIds;
    }

    @JsonProperty(Fields._ID)
    public ObjectId get_id() {
        return _id;
    }

    @JsonProperty(Fields.PREVIOUS_JOB_EXECUTION_IDS)
    public List<Long> getPreviousJobExecutionIds() {
        return previousJobExecutionIds;
    }

    @Override
    @JsonProperty(Fields.JOB_EXECUTION_ID)
    public long getExecutionId() {
        return super.getExecutionId();
    }

    @Override
    @JsonIgnore
    public Properties getJobParameters() {
        return super.getJobParameters();
    }

    @JsonProperty(Fields.JOB_PARAMETERS)
    public MongoProperties getJobProperties() {
        final Properties properties = super.getJobParameters();
        return properties == null ? null : new MongoProperties(properties);
    }

    @Override
    @JsonProperty(Fields.JOB_INSTANCE_ID)
    public long getJobInstanceId() {
        return super.getJobInstanceId();
    }

    @Override
    @JsonProperty(Fields.JOB_NAME)
    public String getJobName() {
        return super.getJobName();
    }

    @Override
    @JsonProperty(Fields.BATCH_STATUS)
    public BatchStatus getBatchStatus() {
        return super.getBatchStatus();
    }

    @Override
    @JsonProperty(Fields.START_TIME)
    public Date getStartTime() {
        return super.getStartTime();
    }

    @Override
    @JsonProperty(Fields.END_TIME)
    public Date getEndTime() {
        return super.getEndTime();
    }

    @Override
    @JsonProperty(Fields.EXIT_STATUS)
    public String getExitStatus() {
        return super.getExitStatus();
    }

    @Override
    @JsonProperty(Fields.CREATE_TIME)
    public Date getCreateTime() {
        return super.getCreateTime();
    }

    @Override
    @JsonProperty(Fields.LAST_UPDATED_TIME)
    public Date getLastUpdatedTime() {
        return super.getLastUpdatedTime();
    }

    @Override
    @JsonProperty(Fields.RESTART_ELEMENT_ID)
    public String getRestartElementId() {
        return super.getRestartElementId();
    }

    @Override
    protected void _toString(final StringBuilder sb) {
        super._toString(sb);
        sb.append(", _id=").append(_id);
    }

    public static Builder from(final MongoJobExecution that) {
        final Builder builder = new Builder();
        _from(builder, that);
        return builder;
    }

    protected static void _from(final _Builder<?> builder, final MongoJobExecution that) {
        builder._id = that._id;
        JobExecutionImpl._from(builder, that);
    }

    @JsonAutoDetect(
            getterVisibility = NONE,
            isGetterVisibility = NONE,
            setterVisibility = NONE,
            fieldVisibility = NONE,
            creatorVisibility = NONE
    )
    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> extends JobExecutionImpl._Builder<T> {
        ObjectId _id;
        List<Long> previousJobExecutionIds = Collections.emptyList();

        @JsonProperty(Fields._ID)
        public T set_id(final ObjectId _id) {
            this._id = _id;
            return (T)this;
        }

        @JsonProperty(Fields.PREVIOUS_JOB_EXECUTION_IDS)
        public T setPreviousJobExecutionIds(final List<Long> previousJobExecutionIds) {
            this.previousJobExecutionIds = previousJobExecutionIds;
            return (T)this;
        }

        @Override
        @JsonIgnore
        public T setJobParameters(final Properties jobParameters) {
            return super.setJobParameters(jobParameters);
        }

        @JsonProperty(Fields.JOB_PARAMETERS)
        public T setJobProperties(final MongoProperties properties) {
            return super.setJobParameters(properties == null ? null : properties.toProperties());
        }

        @Override
        @JsonProperty(Fields.JOB_INSTANCE_ID)
        public T setJobInstanceId(final long jobInstanceId) {
            return super.setJobInstanceId(jobInstanceId);
        }

        @Override
        @JsonProperty(Fields.JOB_EXECUTION_ID)
        public T setJobExecutionId(final long jobExecutionId) {
            return super.setJobExecutionId(jobExecutionId);
        }

        @Override
        @JsonProperty(Fields.JOB_NAME)
        public T setJobName(final String jobName) {
            return super.setJobName(jobName);
        }

        @Override
        @JsonProperty(Fields.BATCH_STATUS)
        public T setBatchStatus(final BatchStatus batchStatus) {
            return super.setBatchStatus(batchStatus);
        }

        @Override
        @JsonProperty(Fields.START_TIME)
        public T setStartTime(final Date startTime) {
            return super.setStartTime(startTime);
        }

        @Override
        @JsonProperty(Fields.END_TIME)
        public T setEndTime(final Date endTime) {
            return super.setEndTime(endTime);
        }

        @Override
        @JsonProperty(Fields.EXIT_STATUS)
        public T setExitStatus(final String exitStatus) {
            return super.setExitStatus(exitStatus);
        }

        @Override
        @JsonProperty(Fields.CREATE_TIME)
        public T setCreateTime(final Date createTime) {
            return super.setCreateTime(createTime);
        }

        @Override
        @JsonProperty(Fields.LAST_UPDATED_TIME)
        public T setLastUpdatedTime(final Date lastUpdatedTime) {
            return super.setLastUpdatedTime(lastUpdatedTime);
        }

        @Override
        @JsonProperty(Fields.RESTART_ELEMENT_ID)
        public T setRestartElementId(final String restartElementId) {
            return super.setRestartElementId(restartElementId);
        }
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder extends _Builder<Builder> {
        @Override
        public MongoJobExecution build() {
            return new MongoJobExecution(this);
        }
    }
}
