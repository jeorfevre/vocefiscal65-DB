package com.rizze.vcfiscal;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.rizze.vcfiscal.tools.S3Bucket;


public class BucketS3Test {
	public Logger logger = Logger.getLogger(BucketS3Test.class);
	
	//@Test
	public void testBucketS3___Test() {
		int ret=0;
		S3Bucket s3 = S3Bucket.i();
		URL u=s3.getSecuredURL("toto/success.jpg");
		try {			
			File n =new File("D:/test/success1.jpg");
			assertTrue(n!=null);
			ret=s3.putDocument(u, new FileInputStream(n));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();	
		}
		logger.info("RETURN CODE==201?"+ret);
		assertTrue(ret==201);
	}

}
