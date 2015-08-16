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
package io.machinecode.chainlink.repository.mongo;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Fields {
    public static final String _ID = "_id";

    public static final String JOB_INSTANCE_ID = "job_instance_id";
    public static final String JOB_NAME = "job_name";
    public static final String JSL_NAME = "jsl_name";
    public static final String CREATE_TIME = "create_time";
    public static final String LATEST_JOB_EXECUTION_ID = "latest_job_execution_id";

    public static final String JOB_EXECUTION_ID = "job_execution_id";
    public static final String JOB_PARAMETERS = "job_parameters";
    public static final String BATCH_STATUS = "batch_status";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String EXIT_STATUS = "exit_status";
    public static final String LAST_UPDATED_TIME = "last_updated_time";
    public static final String RESTART_ELEMENT_ID = "restart_element_id";
    public static final String PREVIOUS_JOB_EXECUTION_IDS = "previous_job_execution_ids";

    public static final String STEP_EXECUTION_ID = "step_execution_id";
    public static final String STEP_NAME = "step_name";
    public static final String UPDATED_TIME = "updated_time";
    public static final String METRICS = "metrics";
    public static final String PERSISTENT_USER_DATA = "persistent_user_data";
    public static final String READER_CHECKPOINT = "reader_checkpoint";
    public static final String WRITER_CHECKPOINT = "writer_checkpoint";

    public static final String PARTITION_PARAMETERS = "partition_parameters";
    public static final String PARTITION_EXECUTION_ID = "partition_execution_id";
    public static final String PARTITION_ID = "partition_id";

    public static final String TYPE = "type";
    public static final String VALUE = "value";

    public static final String K = "k";
    public static final String V = "v";
}
