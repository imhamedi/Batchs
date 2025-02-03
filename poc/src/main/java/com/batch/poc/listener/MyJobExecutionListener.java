package com.batch.poc.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class MyJobExecutionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job Name: " + jobExecution.getJobInstance().getJobName());
        System.out.println("Job Parameters: " + jobExecution.getJobParameters());
        System.out.println("Job Start Time: " + jobExecution.getStartTime());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Job Name: " + jobExecution.getJobInstance().getJobName());
        System.out.println("Job Parameters: " + jobExecution.getJobParameters());
        System.out.println("Job End Time: " + jobExecution.getEndTime());
    }
}
