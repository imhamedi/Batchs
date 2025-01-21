Step
1-Tasklet step : Single task, implementing the Tasklet interface with the execute() method
2-Chunk-based step: Processes data in chunks by reading, processing, and writing in batches for efficient handling.
3-JobLauncher: Orchestrates the launch of a Job with its required parameters.
4-JobRepository: Persistent storage for job metadata and execution context, ensuring restartability and monitoring.

======================
    Spring Batch
======================
           |
  ------------------
  |                |
JobRepository   JobLauncher
  |                |
  |         ------------------
  |         |                |
  |       Job           JobParameters
  |         |
  |    ---------------
  |    |             |
  |  Steps          Metadata
  |    |             
  |    -------------------
  |    |                 |
  |  Tasklet Step   Chunk-based Step
  |    |                 |
  |   Tasklet      ----------------
  |                 |              |
  |             ItemReader   ItemProcessor
  |                 |              |
  |             ItemWriter  (Optional)
  |                 
Execution Context (Job & Step Scoped Data)
           |
Monitoring and Restartability

========================= Update 01/2025 batch 5.x
@EnableBatchProcessing removed
Removed JobBuilderFactory and StepBuilderFactory
Added direct usage of JobBuilder and StepBuilder
Added required dependencies injection (JobRepository and PlatformTransactionManager)

eg :
    @Bean
    public Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Executing step 1...");
                    // Logique métier 
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step firstStep) {
        return new JobBuilder("job1", jobRepository)
                .start(firstStep)
                .build();
    }

