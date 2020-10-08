package web.mini.backend.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import web.mini.backend.BackendApplication;

import java.io.File;
import java.time.LocalDateTime;

@Controller
@RequestMapping("api/v1/")
public class AwsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    @Value("${aws.access_key}")
    private String accessKey;

    @Value("${aws.secret_key}")
    private String secretKey;

    private final AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build();

    private final AWSCredentials credentials = new BasicAWSCredentials(
            accessKey,
            secretKey
    );
    @Value("${aws.bucket_name}")
    private String bucketName;

    @PostMapping("/s3/upload")
    public ResponseEntity<String> uploadFileToS3Bucket(@RequestBody File file) {
        try {
            final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
            LOGGER.info("Uploading file with name= " + uniqueFileName);
            final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
            s3client.putObject(putObjectRequest);
            LOGGER.info("File upload completed.");
            return ResponseEntity.accepted().body("Uploaded Successfully");
        } catch (AmazonServiceException e) {
            LOGGER.info("File upload failed.");
            LOGGER.error("Error: {} while uploading file.", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/s3/{file}")
    public File retrieveFileFromS3Bucket(@PathVariable(name = "file") String file) {
        try {
            final GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, file);
            S3Object s3object = s3client.getObject(getObjectRequest);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            LOGGER.info("File {} retrieval completed.", file);
            return new File(String.valueOf(inputStream));
        } catch (AmazonServiceException e) {
            LOGGER.error("Error: {} while retrieving file.", e.getMessage());
        }
        return null;
    }
}
