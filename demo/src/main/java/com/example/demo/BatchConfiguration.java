package com.example.demo;

import com.example.demostarter.client.AwsClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final AwsClient awsClient;

  public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, AwsClient awsClient) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.awsClient = awsClient;
  }

  @Bean
  public Job testJob(Step firstStep) {
    return jobBuilderFactory.get("test-job")
        .incrementer(new RunIdIncrementer())
        .start(firstStep)
        .build();
  }

  @Bean
  Step firstStep() {
    return stepBuilderFactory.get("first-step").tasklet((stepContribution, chunkContext) -> {
      System.out.println("hello World from first-step");
      awsClient.something();
      return RepeatStatus.FINISHED;
    }).build();
  }
}
