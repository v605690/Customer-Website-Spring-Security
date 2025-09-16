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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                .faultTolerant()
                .retryLimit(3)
                .retry(ObjectOptimisticLockingFailureException.class)
                .skipLimit(50)
                .skip(IllegalArgumentException.class)
                .skip(org.springframework.batch.item.validator.ValidationException.class)
                .noRetry(IllegalArgumentException.class)
                .build();
    }

//    @Bean
//    public Step designationStep(
//            JobRepository jobRepository,
//            ItemReader<Customer> repositoryReader,
//            DesignationProcessor processor,
//            CustomerWriter writer,
//            PlatformTransactionManager transactionManager) {
//
//        // this step converts the designation into matching enum
//        return new StepBuilder("designation-step", jobRepository)
//                .<Customer, Customer>chunk(100, transactionManager)
//                .reader(repositoryReader)
//                .processor(processor)
//                .writer(writer)
//                .faultTolerant()
//                .retryLimit(3)
//                .retry(ObjectOptimisticLockingFailureException.class)
//                .skipLimit(50)
//                .skip(IllegalArgumentException.class)
//                .noRetry(IllegalArgumentException.class)
//                .build();
//    }

    // ItemReaders for this job
    @Bean
    public FlatFileItemReader<Customer> csvReader(
            @Value("${inputFile}") String inputFile) {

        return new FlatFileItemReaderBuilder<Customer>()
                .name("csv-reader")
                .resource(new ClassPathResource(inputFile))
                .delimited()
                .names("id", "fullName", "emailAddress", "age", "address")
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
            customer.setFullName(customer.getFullName().toUpperCase());
            customer.setProcessedData(new Date());
            return customer;
        }
    }

    @Component
    public static class DesignationProcessor implements
            ItemProcessor<Customer, Customer> {

        // validation check
        @Override
        public Customer process(Customer  customer) {
            if (customer.getFullName() == null || customer.getFullName().isEmpty()) {
                throw new IllegalArgumentException("Full name is missing for customer: " + customer.getId());
            }
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
        public void write(@NonNull Chunk<? extends Customer> chunk)
                throws InterruptedException {

            List<Customer> customersToSave = new ArrayList<>();

            chunk.getItems().forEach(customer -> {
                if (customer.getId() != null) {
                    // For existing entities, find the current version and update
                    Customer existingCustomer = customerRepository.findById(customer.getId()).orElse(null);
                    if (existingCustomer != null) {
                        // Update existing entity with new values
                        existingCustomer.setFullName(customer.getFullName());
                        existingCustomer.setEmailAddress(customer.getEmailAddress());
                        existingCustomer.setAge(customer.getAge());
                        existingCustomer.setAddress(customer.getAddress());
                        existingCustomer.setProcessedData(customer.getProcessedData());
                        customersToSave.add(existingCustomer);
                    } else {
                        // Entity doesn't exist in DB, create new customer
                        Customer newCustomer = Customer.builder()
                                .fullName(customer.getFullName())
                                .emailAddress(customer.getEmailAddress())
                                .age(customer.getAge())
                                .address(customer.getAddress())
                                .processedData(customer.getProcessedData())
                                .build();
                        customersToSave.add(newCustomer);
                    }
                }
            });

            customerRepository.saveAll(customersToSave);

            if (SLEEP_TIME != null && SLEEP_TIME > 0) {
            Thread.sleep(SLEEP_TIME);
            }
            System.out.println("Saved customers: " + customersToSave);
        }
    }
}
