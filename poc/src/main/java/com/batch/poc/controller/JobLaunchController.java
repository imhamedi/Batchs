package com.batch.poc.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class JobLaunchController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("firstJob")
    private Job firstJob;

    @Autowired
    @Qualifier("chunkJob")
    private Job job;

    @GetMapping("/launchJob/{id}")
    public void handle(@PathVariable("id") String id) throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        JobParameters jobParameters = new JobParametersBuilder().addString("param", id).toJobParameters();
        jobLauncher.run(this.job, jobParameters);
    }

    @GetMapping("/launchFirstJob/{id}")
    public void handleFirstJob(@PathVariable("id") String id)
            throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        JobParameters jobParameters = new JobParametersBuilder().addString("param", id).toJobParameters();
        jobLauncher.run(this.firstJob, jobParameters);
    }

}
