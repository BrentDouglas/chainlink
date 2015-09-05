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
package io.machinecode.chainlink.core.execution.artifact;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public enum OrderEvent {
    BEFORE_JOB,
    BEFORE_STEP,
    BEFORE_CHUNK,
    ON_CHUNK_ERROR,
    READER_OPEN,
    BEFORE_READ,
    READ,
    AFTER_READ,
    ON_READ_ERROR,
    RETRY_READ_EXCEPTION,
    SKIP_READ,
    READER_CHECKPOINT,
    READER_CLOSE,
    BEFORE_PROCESS,
    PROCESS,
    AFTER_PROCESS,
    ON_PROCESS_ERROR,
    RETRY_PROCESS_EXCEPTION,
    SKIP_PROCESS,
    WRITER_OPEN,
    BEFORE_WRITE,
    WRITE,
    AFTER_WRITE,
    ON_WRITE_ERROR,
    RETRY_WRITE_EXCEPTION,
    SKIP_WRITE,
    WRITER_CHECKPOINT,
    WRITER_CLOSE,
    AFTER_CHUNK,
    AFTER_STEP,
    AFTER_JOB,

    MAP,
    ANALYZE_DATA,
    ANALYZE_STATUS,
    COLLECT,
    REDUCE_BEGIN_STEP,
    REDUCE_BEFORE_COMPLETION,
    REDUCE_ROLLBACK,
    REDUCE_AFTER_COMPLETION,

    BEGIN_TRANSACTION,
    COMMIT_TRANSACTION,
    ROLLBACK_TRANSACTION
}
