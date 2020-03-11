package soffiane.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.util.List;

public class TestConnectionS3 {
	static String bucket_name = "atelier-s3-udemy-soffiane";

	public static void main(String[] args){
		getListObjectInS3Bucket();
		loadFileToS3Bucket("src/main/resources/hello_world.html");
		getListObjectInS3Bucket();
	}

	private static void getListObjectInS3Bucket() {
		System.out.format("Objects in S3 bucket %s:\n", bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
		ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		for (S3ObjectSummary os : objects) {
			System.out.println("* " + os.getKey());
		}
	}

	private static void loadFileToS3Bucket(String file_path) {
		System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
		try {
			s3.putObject(bucket_name, "hello_world.html", new File(file_path));
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}
}
