package org.example.s3lambda;

// To build: mvn clean compile assembly:single

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Collectors;

public class S3EventHandler implements RequestHandler<S3Event, String> {

    private final Properties PROPERTIES = createProperties();

    private final String ACCESS_KEY_ID = PROPERTIES.getProperty("s3.accessKeyId");

    private final String SECRET_ACCESS_KEY_ID = PROPERTIES.getProperty("s3.secretAccessKey");

    private final String REGION = PROPERTIES.getProperty("s3.region");

    private final BasicAWSCredentials AWS_CREDENTIALS = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY_ID);

    private final TwitSolrClient SOLR_CLIENT = new TwitSolrClient();

    private final AmazonS3 S3_CLIENT = AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.fromName(REGION))
            .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
            .build();
    private static final Logger LOGGER = LoggerFactory.getLogger(S3EventHandler.class);

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        String bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String fileName = s3Event.getRecords().get(0).getS3().getObject().getKey();
        LOGGER.info("File - {} uploaded into {} bucket at {} ", fileName, bucketName,
                s3Event.getRecords().get(0).getEventTime());
        try (InputStream s3ObjectInputStream = S3_CLIENT.getObject(bucketName, fileName).getObjectContent()) {
            String content = new BufferedReader(new InputStreamReader(s3ObjectInputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            LOGGER.info("File content {} ", content);
            SOLR_CLIENT.addJsonDocToSolrIndex(SOLR_CLIENT.parseStringToJson(content));
            LOGGER.info("New document added to solr.");

        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading content of the file";
        }
        return "Done!";
    }

    public Properties createProperties() {
        try (InputStream inputStream = TwitSolrClient.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            return prop;
        } catch (IOException e) {
            throw new RuntimeException("Error with properties file!");
        }
    }
}
