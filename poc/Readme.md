mvn clean package pour générer le jar

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

=============
1. Batch Status
Le Batch Status représente l'état global de l'exécution d'un Job ou d'un Step du point de vue du framework Spring Batch. C'est une vue d'ensemble de l'exécution, souvent utilisée pour surveiller et diagnostiquer les processus.

Valeurs possibles pour Batch Status :
STARTING : Le job ou le step est en cours de démarrage.
STARTED : Le job ou le step a démarré et est en cours d'exécution.
COMPLETED : Le job ou le step s'est terminé avec succès.
STOPPING : Le job ou le step est en cours d'arrêt (interruption manuelle ou conditionnelle).
STOPPED : Le job ou le step a été arrêté avant de se terminer.
FAILED : Le job ou le step s'est terminé avec une erreur.
UNKNOWN : L'état n'est pas identifiable.
2. Exit Status
Le Exit Status fournit une information plus granulaire et contextuelle sur la façon dont un Step ou un Job s'est terminé. Contrairement au Batch Status, il est destiné à transmettre des messages personnalisés ou des informations spécifiques sur le résultat d'une exécution.

Valeurs par défaut pour Exit Status :
COMPLETED : Indique que le step ou le job s'est terminé avec succès (même si des exceptions ou des warnings mineurs ont été gérés).
FAILED : Indique qu'il y a eu une erreur ou un échec.
| Aspect           | Batch Status                          | Exit Status                                            |
|==================|=======================================|==========================================|=============|
| But              | État global de l'exécution            | Résultat détaillé ou spécifique                        |
| Valeurs          | Pré-définies par Spring Batch         | Pré-définies + personnalisables                        |
| Scope            | Utilisé pour surveiller les jobs/steps| Utilisé pour la logique conditionnelle ou des messages |
| Personnalisable  | Non                                   | Oui                                                    |

=============Custom exit status
On peut utiliser soit les listener (exemple ici : FirstStepExecutionListener)
ou les job execution decider

=========chunck
Le chunk-oriented processing repose sur le triptyque suivant :

ItemReader : Lit un ou plusieurs éléments d’une source (par exemple, une base de données, un fichier CSV, etc.).
ItemProcessor : Effectue des transformations ou des validations sur chaque élément lu (facultatif).
ItemWriter : Écrit les éléments traités vers une destination (par exemple, un autre fichier, une base de données, etc.).
L’ensemble du traitement est configuré en définissant une taille de chunk.

