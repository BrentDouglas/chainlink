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
package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.machinecode.chainlink.core.repository.JobInstanceImpl;
import org.bson.types.ObjectId;

import java.util.Date;

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
@JsonDeserialize(builder = MongoJobInstance.Builder.class)
public class MongoJobInstance extends JobInstanceImpl {
    private static final long serialVersionUID = 1L;

    private final ObjectId _id;
    private final Long latestJobInstanceId;

    public MongoJobInstance(final _Builder builder) {
        super(builder);
        this._id = builder._id;
        this.latestJobInstanceId = builder.latestJobInstanceId;
    }

    @JsonProperty(Fields._ID)
    public ObjectId get_id() {
        return _id;
    }

    @JsonProperty(Fields.LATEST_JOB_EXECUTION_ID)
    public Long getLatestJobInstanceId() {
        return latestJobInstanceId;
    }

    @Override
    @JsonProperty(Fields.JOB_INSTANCE_ID)
    public long getInstanceId() {
        return super.getInstanceId();
    }

    @Override
    @JsonProperty(Fields.JOB_NAME)
    public String getJobName() {
        return super.getJobName();
    }

    @Override
    @JsonProperty(Fields.JSL_NAME)
    public String getJslName() {
        return super.getJslName();
    }

    @Override
    @JsonProperty(Fields.CREATE_TIME)
    public Date getCreateTime() {
        return super.getCreateTime();
    }

    @Override
    protected void _toString(final StringBuilder sb) {
        super._toString(sb);
        sb.append(", _id=").append(_id);
    }

    @JsonAutoDetect(
            getterVisibility = NONE,
            isGetterVisibility = NONE,
            setterVisibility = NONE,
            fieldVisibility = NONE,
            creatorVisibility = NONE
    )
    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> extends JobInstanceImpl._Builder<T> {
        ObjectId _id;
        Long latestJobInstanceId;

        @JsonProperty(Fields._ID)
        public T set_id(final ObjectId _id) {
            this._id = _id;
            return (T)this;
        }

        @Override
        @JsonProperty(Fields.JOB_INSTANCE_ID)
        public T setJobInstanceId(final long instanceId) {
            return super.setJobInstanceId(instanceId);
        }

        @Override
        @JsonProperty(Fields.JOB_NAME)
        public T setJobName(final String jobName) {
            return super.setJobName(jobName);
        }

        @Override
        @JsonProperty(Fields.JSL_NAME)
        public T setJslName(final String jslName) {
            return super.setJslName(jslName);
        }

        @Override
        @JsonProperty(Fields.CREATE_TIME)
        public T setCreateTime(final Date createTime) {
            return super.setCreateTime(createTime);
        }

        @JsonProperty(Fields.LATEST_JOB_EXECUTION_ID)
        public T setLatestJobInstanceId(final Long latestJobInstanceId) {
            this.latestJobInstanceId = latestJobInstanceId;
            return (T)this;
        }
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder extends _Builder<Builder> {
        @Override
        public MongoJobInstance build() {
            return new MongoJobInstance(this);
        }
    }
}
