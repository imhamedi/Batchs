package com.batch.poc.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import com.batch.poc.service.IncrementService;

@Configuration
public class BatchConfiguration {
    private final IncrementService incrementService;

    public BatchConfiguration(IncrementService incrementService) {
        this.incrementService = incrementService;
    }

    private int variable;

    @Bean
    public Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Executing step 1...");
                    incrementService.incrementVariable();
                    variable = incrementService.getVariable();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step secondStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Executing step 2...");
                    incrementService.multiplyVariableBy10(variable);
                    variable = incrementService.getVariable();
                    System.out.println("Exécution step 2 terminée avec succès");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step thirdStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Executing step 3...");
                    incrementService.multiplyVariableBy10(variable);
                    System.out.println("Exécution step 3 terminée avec succès");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job firstJob(JobRepository jobRepository, Step firstStep) {
        return new JobBuilder("job1", jobRepository)
                // .preventRestart() ==> empêcher les restarts pour la memeinstance (meme
                // parametres d'entree) en cas d'erreur
                .start(firstStep)
                .next(secondStep(jobRepository, null))
                .next(thirdStep(jobRepository, null))
                .build();
    }
}
