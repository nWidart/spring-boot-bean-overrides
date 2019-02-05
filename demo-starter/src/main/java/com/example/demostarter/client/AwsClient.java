package com.example.demostarter.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AwsClient {

  private final AmazonS3 s3client;

  public AwsClient(AmazonS3 s3client) {
    this.s3client = s3client;
  }

  public void something() {
    List<Bucket> buckets = s3client.listBuckets();
    System.out.println(buckets);
  }
}
