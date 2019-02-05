package com.example.demostarter.config;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Data
@ConfigurationProperties
@PropertySource(value = {
    "classpath:aws.properties",
    "classpath:aws-${spring.profiles.active}.properties"
}, ignoreResourceNotFound = true)
@Slf4j
public class AWSProperties {
  @Value("${s3.credentials.bucketName}")
  private String bucketName;
  @Value("${s3.credentials.accessKey}")
  private String accessKey;
  @Value("${s3.credentials.secretKey}")
  private String secretKey;
  @Value("${s3.credentials.region}")
  private String region;

  @Primary
  @Bean
  public AmazonS3 s3client() {

    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

    return AmazonS3ClientBuilder.standard()
        .withRegion(Regions.fromName(region))
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build();
  }

  @Bean
  public TransferManager transferManager() {
    TransferManager tm = TransferManagerBuilder.standard()
        .withS3Client(s3client())
        .build();

    int oneDay = 1000 * 60 * 60 * 24;
    Date oneDayAgo = new Date(System.currentTimeMillis() - oneDay);

    try {
      tm.abortMultipartUploads(bucketName, oneDayAgo);

    } catch (AmazonClientException e) {
      log.error("Unable to upload file, upload was aborted, reason: " + e.getMessage());
    }

    return tm;
  }
}
