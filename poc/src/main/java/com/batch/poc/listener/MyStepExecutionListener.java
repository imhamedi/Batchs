package com.batch.poc.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class MyStepExecutionListener implements StepExecutionListener {
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Step Name: " + stepExecution.getStepName());
		System.out.println("Step Exit Status: " + stepExecution.getExitStatus());
		System.out.println("Step Start Time: " + stepExecution.getStartTime());
		System.out.println(stepExecution.getStepName() + " executed on thread " + Thread.currentThread().getName());
	}

	@AfterStep
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("Step Name: " + stepExecution.getStepName());
		System.out.println("Step Exit Status: " + stepExecution.getExitStatus());
		System.out.println("Step End Time: " + stepExecution.getEndTime());
		System.out.println(stepExecution.getStepName() + " executed on thread " + Thread.currentThread().getName());
		return null;
	}

}
