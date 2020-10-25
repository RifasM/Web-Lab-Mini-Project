package web.mini.backend.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import web.mini.backend.BackendApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class AwsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    /*
    Make a file .aws/config in home directory
    Fill it with
    [default]
    aws_access_key_id={YOUR_ACCESS_KEY_ID}
    aws_secret_access_key={YOUR_SECRET_ACCESS_KEY}

    and then execute

    export AWS_CREDENTIAL_PROFILES_FILE=/home/ubuntu/.aws/config
     */

    private final AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.AP_SOUTH_1)
            .build();

    private final String bucketName = "web-mini";

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            LOGGER.error("Error converting the multi-part file to file = " + ex.getMessage());
        }
        return file;
    }

    public ResponseEntity<String> uploadFileToS3Bucket(MultipartFile multipartFile, String bucketSubName) {
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            final String uniqueFileName = (LocalDateTime.now() + "_" + file.getName()).replace(" ", "_");
            LOGGER.info("Uploading file with name= " + uniqueFileName);
            final PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName +
                    "/" + bucketSubName, uniqueFileName, file);
            this.s3client.putObject(putObjectRequest);
            LOGGER.info("File upload completed.");
            file.delete();
            return ResponseEntity.ok().body(uniqueFileName);
        } catch (AmazonServiceException e) {
            LOGGER.info("File upload failed.");
            LOGGER.error("Error: {} while uploading file.", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/s3/{bucketSubName}/{file}")
    public File retrieveFileFromS3Bucket(@PathVariable(name = "file") String file,
                                         @PathVariable(name = "bucketSubName") String bucketSubName) {
        try {
            final GetObjectRequest getObjectRequest = new GetObjectRequest(this.bucketName
                    + "/" + bucketSubName, file);
            S3Object s3object = this.s3client.getObject(getObjectRequest);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            LOGGER.info("File {} retrieval completed.", file);
            return new File(String.valueOf(inputStream));
        } catch (AmazonServiceException e) {
            LOGGER.error("Error: {} while retrieving file.", e.getMessage());
        }
        return null;
    }

    @DeleteMapping("/s3/{bucketSubName}/{file}")
    public ResponseEntity<String> deleteFileFromS3Bucket(@PathVariable(name = "file") String file,
                                                         @PathVariable(name = "bucketSubName") String bucketSubName) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucketName
                    + "/" + bucketSubName, file);
            this.s3client.deleteObject(deleteObjectRequest);
            return ResponseEntity.ok().body("true");
        } catch (AmazonServiceException e) {
            LOGGER.error("Error: {} while deleting file.", e.getMessage());
        }

        return ResponseEntity.status(500).body("Error, check logs");
    }
}
