package com.batch.poc.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.poc.decider.FirstJobExecutionDecider;
import com.batch.poc.listner.FirstStepExecutionListner;
import com.batch.poc.service.IncrementService;

@Configuration
public class BatchConfiguration {
    private final IncrementService incrementService;

    @Bean
    public StepExecutionListener firstStepExecutionListner() {
        return new FirstStepExecutionListner();

    }

    @Bean
    public JobExecutionDecider firstJobExecutionDecider() {
        return new FirstJobExecutionDecider();

    }

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

    /*
     * avec exception
     * 
     * @Bean
     * public Step secondStep(JobRepository jobRepository,
     * PlatformTransactionManager transactionManager) {
     * return new StepBuilder("step2", jobRepository)
     * .tasklet((contribution, chunkContext) -> {
     * System.out.println("Executing step 2...");
     * incrementService.divideVariableBy0(variable);
     * variable = incrementService.getVariable();
     * System.out.println("Exécution step 2 terminée avec succès");
     * return RepeatStatus.FINISHED;
     * }, transactionManager)
     * .build();
     * }
     */

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
                // .listener(firstStepExecutionListner()) // si on veut utiliser le listerner
                // pour
                // changer le status exit
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
    public Step fourthStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Executing step 4...");
                    incrementService.multiplyVariableBy10(variable);
                    System.out.println("Exécution step 4 terminée avec succès");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job firstJob(JobRepository jobRepository, Step firstStep) {
        return new JobBuilder("firstjob", jobRepository)
                // .preventRestart() ==> empêcher les restarts pour la memeinstance (meme
                // parametres) en cas d'erreur
                .start(firstStep)
                .next(secondStep(jobRepository, null))
                .next(thirdStep(jobRepository, null))
                .next(fourthStep(jobRepository, null))
                .build();
    }

    // gestion des flows : si secondstep fail go to fourthstep si ok go to stepthree
    /*
     * Avec le listener
     * 
     * @Bean
     * public Job firstJob(JobRepository jobRepository, Step firstStep) {
     * return new JobBuilder("firstjob", jobRepository)
     * .start(firstStep)
     * .on("COMPLETED").to(secondStep(null, null))
     * .from(secondStep(null, null)).on("TEST_STATUS").to(thirdStep(null, null))
     * // .from(secondStep(null, null)).on("FAILED").to(fourthStep(null, null))
     * .from(secondStep(null, null)).on("*").to(fourthStep(null, null)) // .* veut
     * // dire n'importe quel status à
     * // part COMPLETED
     * .end()
     * .build();
     * }
     */
    /*
     * // avec le decider ==> il ne stocke pas le statut dans la base de données
     * donc utilité très réduite, pas util à mon sens
     * 
     * @Bean
     * public Job firstJob(JobRepository jobRepository, Step firstStep) {
     * return new JobBuilder("firstjob", jobRepository)
     * .start(firstStep)
     * .on("COMPLETED").to(firstJobExecutionDecider())
     * .on("STATUS_DECIDER").to(secondStep(jobRepository, null))
     * .from(firstJobExecutionDecider())
     * .on("*").to(thirdStep(jobRepository, null))
     * .end()
     * .build();
     * }
     */
}
