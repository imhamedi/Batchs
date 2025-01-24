package com.batch.poc.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.poc.domain.OSProduct;
import com.batch.poc.domain.Product;
import com.batch.poc.domain.ProductFieldSetMapper;
import com.batch.poc.domain.ProductRowMapper;
import com.batch.poc.domain.ProductValidator;
import com.batch.poc.processor.FilterProductItemProcessor;
import com.batch.poc.processor.MyProductItemProcessor;
import com.batch.poc.reader.ProductNameItemReader;

@Configuration
public class BatchConfigurationChunck {
    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<String> itemReader() {
        List<String> productList = new ArrayList<>();
        productList.add("Product 1");
        productList.add("Product 2");
        productList.add("Product 3");
        productList.add("Product 4");
        productList.add("Product 5");
        productList.add("Product 6");
        productList.add("Product 7");
        productList.add("Product 8");
        return new ProductNameItemReader(productList);
    }

    @Value("${file.input.product}")
    private String productInputFilePath;

    @Bean
    public ItemReader<Product> flatFileItemReader() {
        FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource(productInputFilePath));

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("product_id", "product_name", "product_category", "product_price");

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

        itemReader.setLineMapper(lineMapper);

        return itemReader;
    }

    @Bean
    public ItemReader<Product> jdbcCursorItemReader() {
        JdbcCursorItemReader<Product> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("select * from product_details order by product_id");
        itemReader.setRowMapper(new ProductRowMapper());
        return itemReader;
    }

    @Bean
    public ItemReader<Product> jdbcPagingItemReader() throws Exception {
        JdbcPagingItemReader<Product> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);

        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("select product_id, product_name, product_category, product_price");
        factory.setFromClause("from product_details");
        factory.setSortKey("product_id");

        itemReader.setQueryProvider(factory.getObject());
        itemReader.setRowMapper(new ProductRowMapper());
        itemReader.setPageSize(3);

        return itemReader;
    }

    @Value("${file.output.product}")
    private String productOutputFilePath;

    @Bean
    public ItemWriter<Product> flatFilItemWriter() {
        FlatFileItemWriter<Product> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setResource(new FileSystemResource(productOutputFilePath));
        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        BeanWrapperFieldExtractor<Product> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] { "productId", "productName", "productCategory", "productPrice" });
        lineAggregator.setFieldExtractor(fieldExtractor);
        itemWriter.setLineAggregator(lineAggregator);
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Product> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Product> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(
                "insert into product_details_output values (:productId,:productName,:productCategory,:productPrice)");
        // itemWriter.setItemPreparedStatementSetter(new
        // ProductItemPreparedStatementSetter()); lit le résultat de reader
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()); // plus besoin
                                                                                                          // de la
                                                                                                          // classe
                                                                                                          // ProductItemPreparedStatementSetter
        return itemWriter;
    }

    // jdbcBatchItemWriterOsProduct
    @Bean
    public JdbcBatchItemWriter<OSProduct> jdbcBatchItemWriterOsProduct() {
        JdbcBatchItemWriter<OSProduct> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(
                "insert into os_product_details values (:productId, :productName, :productCategory, :productPrice, :taxPercent, :sku, :shippingRate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return itemWriter;
    }

    @Bean
    public ItemProcessor<Product, Product> filterProductItemProcessor() {
        return new FilterProductItemProcessor();
    }

    @Bean
    public ItemProcessor<Product, OSProduct> transformProductItemProcessor() {
        return new MyProductItemProcessor();
    }

    @Bean
    public ValidatingItemProcessor<Product> validateProductItemProcessor() {
        ValidatingItemProcessor<Product> validatingItemProcessor = new ValidatingItemProcessor<>(
                new ProductValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public BeanValidatingItemProcessor<Product> validateProductItemProcessorWithBeans() {
        BeanValidatingItemProcessor<Product> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
        beanValidatingItemProcessor.setFilter(true);
        return beanValidatingItemProcessor;
    }

    @SuppressWarnings("unchecked")
    @Bean
    public CompositeItemProcessor<Product, OSProduct> itemProcessor() {
        CompositeItemProcessor<Product, OSProduct> itemProcessor = new CompositeItemProcessor<>();
        @SuppressWarnings("rawtypes")
        List itemProcessors = new ArrayList();
        itemProcessors.add(validateProductItemProcessorWithBeans());
        itemProcessors.add(filterProductItemProcessor());
        itemProcessors.add(transformProductItemProcessor());
        itemProcessor.setDelegates(itemProcessors);
        return itemProcessor;
    }

    @Bean
    public Step chunkStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkBasedStep1", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(itemReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        System.out.println("Chunk-processing Started");
                        chunk.getItems().forEach(System.out::println);
                        System.out.println("Chunk-processing Ended");
                    }
                })
                .build();

    }

    @Bean
    public Step chunkStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkBasedStep2", jobRepository)
                .<Product, Product>chunk(3, transactionManager)
                .reader(flatFileItemReader())
                .writer(flatFilItemWriter())
                .build();

    }

    @Bean
    public Step chunkStep3(JobRepository jobRepository, PlatformTransactionManager transactionManager)
            throws Exception {
        return new StepBuilder("chunkBasedStep3", jobRepository)
                .<Product, Product>chunk(3, transactionManager)
                .reader(jdbcPagingItemReader())
                .writer(jdbcBatchItemWriter())
                .build();

    }

    // Add processor
    @Bean
    public Step chunkStep4(JobRepository jobRepository, PlatformTransactionManager transactionManager)
            throws Exception {
        return new StepBuilder("chunkBasedStep4", jobRepository)
                .<Product, OSProduct>chunk(3, transactionManager)
                .reader(jdbcPagingItemReader())
                .processor(transformProductItemProcessor())
                .writer(jdbcBatchItemWriterOsProduct())
                .build();

    }

    @Bean
    public Step chunkStep5(JobRepository jobRepository, PlatformTransactionManager transactionManager)
            throws Exception {
        return new StepBuilder("chunkBasedStep5", jobRepository)
                .<Product, Product>chunk(3, transactionManager)
                .reader(jdbcPagingItemReader())
                .processor(filterProductItemProcessor())
                .writer(jdbcBatchItemWriter())
                .build();

    }

    @Bean // validation avec validateProductItemProcessor
    public Step chunkStep6(JobRepository jobRepository, PlatformTransactionManager transactionManager)
            throws Exception {
        return new StepBuilder("chunkBasedStep6", jobRepository)
                .<Product, Product>chunk(3, transactionManager)
                .reader(jdbcPagingItemReader())
                .processor(validateProductItemProcessor())
                .writer(jdbcBatchItemWriter())
                .build();

    }

    @Bean // validation avec BeanValidatingItemProcessor
    public Step chunkStep7(JobRepository jobRepository, PlatformTransactionManager transactionManager)
            throws Exception {
        return new StepBuilder("chunkBasedStep7", jobRepository)
                .<Product, Product>chunk(3, transactionManager)
                .reader(jdbcPagingItemReader())
                .processor(validateProductItemProcessorWithBeans())
                .writer(jdbcBatchItemWriter())
                .build();

    }

    @Bean // enchainement de sprocessus
    public Step chunkStep8(JobRepository jobRepository, PlatformTransactionManager transactionManager)
            throws Exception {
        return new StepBuilder("chunkBasedStep8", jobRepository)
                .<Product, OSProduct>chunk(3, transactionManager)
                .reader(jdbcPagingItemReader())
                .processor(itemProcessor())
                .writer(jdbcBatchItemWriterOsProduct())
                .build();

    }

    @Bean
    public Job chunkJob(JobRepository jobRepository, Step chunkStep1) throws Exception {
        return new JobBuilder("crunchjob", jobRepository)
                // .preventRestart() ==> empêcher les restarts pour la memeinstance (meme
                // parametres) en cas d'erreur
                // .start(chunkStep1)
                // .next(chunkStep2(jobRepository, null))
                // .next(chunkStep3(jobRepository, null))
                // .next(chunkStep4(jobRepository, null))
                .start(chunkStep8(null, null))
                .build();
    }

}
