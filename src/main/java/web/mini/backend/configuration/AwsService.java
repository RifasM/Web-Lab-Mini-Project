package web.mini.backend.configuration;

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
import web.mini.backend.BackendApplication;

import java.io.File;
import java.time.LocalDateTime;

public class AwsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);
    private final AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build();
    @Value("${aws.access_key}")
    private String accessKey;
    @Value("${aws.secret_key}")
    private String secretKey;
    private final AWSCredentials credentials = new BasicAWSCredentials(
            accessKey,
            secretKey
    );

    public boolean uploadFileToS3Bucket(String bucketName, File file) {
        try {
            final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
            LOGGER.info("Uploading file with name= " + uniqueFileName);
            final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
            s3client.putObject(putObjectRequest);
            LOGGER.info("File upload completed.");
            return true;
        } catch (AmazonServiceException e) {
            LOGGER.info("File upload failed.");
            LOGGER.error("Error: {} while uploading file.", e.getMessage());
            return false;
        }
    }

    public File retrieveFileFromS3Bucket(String bucketName, String file) {
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
