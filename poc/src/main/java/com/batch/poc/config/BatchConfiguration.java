package com.batch.poc.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.poc.decider.MyJobExecutionDecider;
import com.batch.poc.listener.MyStepExecutionListener;

@Configuration
public class BatchConfiguration {
	
	@Bean
	public StepExecutionListener myStepExecutionListener() {
		return new MyStepExecutionListener();
	}
	
	@Bean
	public JobExecutionDecider decider() {
		return new MyJobExecutionDecider();
	}
	
	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step1", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step1 executed!!");
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step2", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step2 executed!!");
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step3", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step3 executed!!");
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
		return new StepBuilder("step4", jobRepository).tasklet(new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("step4 executed!!");
				return RepeatStatus.FINISHED;
			}
		}, transactionManger).build();
	}
	
	@Bean
	public Flow flow1(Step step3, Step step4) {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow1");
		flowBuilder.start(step3)
				.next(step4)
				.end();
		return flowBuilder.build();
	}
	
	@Bean
	public Job firstJob(JobRepository jobRepository, Step step1, Step step2, Flow flow1) {
		return new JobBuilder("job1", jobRepository)
				.start(step1)
				.next(step2)
					.on("COMPLETED").to(flow1)
				.end()
				.build();
	}
}
