package deployment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentTier;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class Deployment {

	static AmazonEC2 ec2;
	static AmazonS3 s3;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		AWSCredentials credentials = new PropertiesCredentials(new File("AwsCredentials.properties"));	
		ec2 = new AmazonEC2Client(credentials);
		s3 = new AmazonS3Client(credentials);
		ec2.setEndpoint("ec2.us-west-2.amazonaws.com");
		AWSElasticBeanstalk beanstalk = new AWSElasticBeanstalkClient(credentials);
		beanstalk.setEndpoint("elasticbeanstalk.us-west-2.amazonaws.com");
		
		// Application info
		String warFilePath = "/Users/Yi/Desktop/TweetMap.war";
		String applicationName = "tweet-map";
		String applicationDescription = "A twitter heat map deployed by Java programmatically.";
		
		// Upload .war file to S3
		System.out.println("Uploading " + warFilePath + " to S3...");
		s3.putObject("elasticbeanstalk-us-west-2-665550041439", applicationName + ".war", new File(warFilePath));
		
		// Create an application with application name and description
		System.out.println("Creating new application...");
		CreateApplicationRequest createApplicationRequest = new CreateApplicationRequest();
		createApplicationRequest.withApplicationName(applicationName);
		createApplicationRequest.withDescription(applicationDescription);
		beanstalk.createApplication(createApplicationRequest);
		
		// Create application version
		System.out.println("Creating new application version...");
		S3Location sourceBundle = new S3Location("elasticbeanstalk-us-west-2-665550041439", applicationName + ".war");
		CreateApplicationVersionRequest createApplicationVersionRequest = new CreateApplicationVersionRequest();
		createApplicationVersionRequest.withApplicationName(applicationName)
									   .withVersionLabel(applicationName)
									   .withAutoCreateApplication(true)
									   .withSourceBundle(sourceBundle);
		beanstalk.createApplicationVersion(createApplicationVersionRequest);
		
		// Specify configuration settings
		Collection<ConfigurationOptionSetting> settings = new ArrayList<ConfigurationOptionSetting>();
		settings.add(new ConfigurationOptionSetting("aws:autoscaling:asg", "MaxSize", "1"));
		settings.add(new ConfigurationOptionSetting("aws:elb:loadbalancer", "CrossZone", "true"));
		settings.add(new ConfigurationOptionSetting("aws:autoscaling:launchconfiguration", "InstanceType", "t2.micro"));
		settings.add(new ConfigurationOptionSetting("aws:elb:policies", "ConnectionDrainingEnabled", "true"));
		settings.add(new ConfigurationOptionSetting("aws:elb:policies", "ConnectionDrainingTimeout", "20"));

		// Create environment with specified configuration settings
		System.out.println("Creating new environment...");
		CreateEnvironmentRequest createEnvironmentRequest = new CreateEnvironmentRequest();
		createEnvironmentRequest.withTier(new EnvironmentTier().withName("WebServer").withType("Standard"))
								.withApplicationName(applicationName)
								.withEnvironmentName(applicationName.toLowerCase())
								.withOptionSettings(settings)
								.withCNAMEPrefix(applicationName.toLowerCase() + "-by-yi")
								.withVersionLabel(applicationName)
								.withSolutionStackName("64bit Amazon Linux 2014.09 v1.2.0 running Tomcat 8 Java 8");
		beanstalk.createEnvironment(createEnvironmentRequest);
		
		// Deploy application
		System.out.println("Wait 5 mins for instance to be ready and update application version...");
		Thread.sleep(300000);
		UpdateEnvironmentRequest updateEnvironmentRequest = new UpdateEnvironmentRequest();
		updateEnvironmentRequest.withEnvironmentName(applicationName.toLowerCase()).withVersionLabel(applicationName);
		beanstalk.updateEnvironment(updateEnvironmentRequest);
		
		System.out.println("Done!");
	}
	
}
