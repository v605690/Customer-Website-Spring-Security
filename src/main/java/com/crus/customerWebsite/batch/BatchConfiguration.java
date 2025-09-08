package com.crus.customerWebsite.batch;

import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.repos.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;
import java.util.Map;

@Configuration
public class BatchConfiguration {

    @Bean
    public Job job(
            JobRepository jobRepository,
            Step nameStep,
            Step designationStep) {

        return new JobBuilder("customer-loader-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(nameStep)
                .next(designationStep)
                .build();
    }

    // Job defined in detail
    @Bean
    public Step nameStep(
            JobRepository jobRepository,
            ItemReader<Customer> csvReader,
            NameProcessor processor,
            CustomerWriter writer,
            PlatformTransactionManager transactionManager) {

        // this step reads the csv and writes the entries into the database
        return new StepBuilder("name-step", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(csvReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step designationStep(
            JobRepository jobRepository,
            ItemReader<Customer> repositoryReader,
            DesignationProcessor processor,
            CustomerWriter writer,
            PlatformTransactionManager transactionManager) {

        // this step converts the designation into matching enum
        return new StepBuilder("designation-step", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(repositoryReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

    // ItemReaders for this job
    @Bean
    public FlatFileItemReader<Customer> csvReader(
            @Value("${inputFile}") String inputFile) {

        return new FlatFileItemReaderBuilder<Customer>()
                .name("csv-reader")
                .resource(new ClassPathResource(inputFile))
                .delimited()
                .names("id", "name", "designation")
                .linesToSkip(1)
                .fieldSetMapper(
                        new BeanWrapperFieldSetMapper<>() {
                            {setTargetType(Customer.class);}
                        })
                .build();
    }

    @Bean
    public RepositoryItemReader<Customer> repositoryReader(
            CustomerRepository customerRepository) {

        return new RepositoryItemReaderBuilder<Customer>()
                .repository(customerRepository)
                .methodName("findAll")
                .sorts(Map.of("id", Sort.Direction.ASC))
                .name("repository-reader")
                .build();
    }

    // ItemProcessors
    @Component
    public static class NameProcessor implements
            ItemProcessor<Customer, Customer> {

        // process the name of the employee
        @Override
        public Customer process(Customer customer) {
            customer.setFullName();
            customer.setFullName(customer.getFullName().toUpperCase());
            customer.setNameUpdatedAt(new Date());
            return customer;
        }
    }

    @Component
    public static class DesignationProcessor implements
            ItemProcessor<Customer, Customer> {

        // converts the designations into the enum you defined earlier
        @Override
        public Customer process(Customer  customer) {
            customer.setDesignation(
                    Designation.getByCode(
                            customer.getDesignation()).getTitle());
            customer.setDesignationUpdatedAt(new Date());
            return customer;
        }
    }

    // ItemWriter
    @Component
    public static class CustomerWriter implements ItemWriter<Customer> {

        @Autowired
        private CustomerRepository customerRepository;

        @Value("${sleepTime}")
        private Integer SLEEP_TIME;

        @Override
        public void write(@NonNull Chunk<? extends Customer> customer)
                throws InterruptedException {

            customerRepository.saveAll(customer);
            Thread.sleep(SLEEP_TIME);
            System.out.println("Saved customers: " + customer);
        }
    }
}
