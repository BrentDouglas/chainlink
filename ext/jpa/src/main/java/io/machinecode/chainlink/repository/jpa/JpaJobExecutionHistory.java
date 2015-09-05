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
package io.machinecode.chainlink.repository.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Entity
@Table(name = "job_execution_history")
public class JpaJobExecutionHistory {
    private long id;
    private JpaJobExecution jobExecution;
    private JpaJobExecution previousJobExecution;

    public JpaJobExecutionHistory() {
    }

    public JpaJobExecutionHistory(final JpaJobExecutionHistory builder) {
        this.jobExecution = builder.jobExecution;
        this.previousJobExecution = builder.previousJobExecution;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public JpaJobExecutionHistory setId(final long id) {
        this.id = id;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id")
    public JpaJobExecution getJobExecution() {
        return jobExecution;
    }

    public JpaJobExecutionHistory setJobExecution(final JpaJobExecution jobExecution) {
        this.jobExecution = jobExecution;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_job_execution_id")
    public JpaJobExecution getPreviousJobExecution() {
        return previousJobExecution;
    }

    public JpaJobExecutionHistory setPreviousJobExecution(final JpaJobExecution previousJobExecution) {
        this.previousJobExecution = previousJobExecution;
        return this;
    }
}
