package javax.batch.operations;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobOperator {

    Set<String> getJobNames() throws JobSecurityException;

    int getJobInstanceCount(String jobName) throws NoSuchJobException, JobSecurityException;

    List<JobInstance> getJobInstances(String jobName, int start, int count)throws NoSuchJobException, JobSecurityException;

    List<Long> getRunningExecutions(String jobName) throws NoSuchJobException, JobSecurityException;

    Properties getParameters(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    long start(String jslName, Properties parameters) throws JobStartException, JobSecurityException;

    long restart(long jobExecutionId, Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException;

    void stop(long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException;

    void abandon(long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException;

    JobInstance getJobInstance(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    List<JobExecution> getJobExecutions(JobInstance jobInstance) throws NoSuchJobInstanceException, JobSecurityException;

    JobExecution getJobExecution(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    List<StepExecution> getStepExecutions(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;
}
