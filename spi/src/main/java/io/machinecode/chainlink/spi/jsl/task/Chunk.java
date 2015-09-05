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
package io.machinecode.chainlink.spi.jsl.task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Chunk extends Task {

    String ELEMENT = "chunk";

    String TEN = "10";
    String ZERO = "0";
    String MINUS_ONE = "-1";

    String getCheckpointPolicy();

    String getItemCount();

    String getTimeLimit();

    String getSkipLimit();

    String getRetryLimit();

    ItemReader getReader();

    ItemProcessor getProcessor();

    ItemWriter getWriter();

    CheckpointAlgorithm getCheckpointAlgorithm();

    ExceptionClassFilter getSkippableExceptionClasses();

    ExceptionClassFilter getRetryableExceptionClasses();

    ExceptionClassFilter getNoRollbackExceptionClasses();

    public static final class CheckpointPolicy {
        public static final String ITEM = "item";
        public static final String CUSTOM = "custom";
    }
}
