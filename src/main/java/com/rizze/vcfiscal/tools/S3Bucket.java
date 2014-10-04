package com.rizze.vcfiscal.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;


/**
 * @since 0.7.4
 * @author Jean-Emmanuel
 *
 */
public class S3Bucket{
	private static Logger logger = Logger.getLogger(S3Bucket.class);
	public Helper h = new Helper();
	private static S3Bucket instance=null;
	
	
	private S3Bucket() {
		try {
			AWSCredentials credentials = new BasicAWSCredentials(h.AWSAccessKeyId,h.AWSSecretKey);
			ClientConfiguration conf= new ClientConfiguration();
			conf.setMaxErrorRetry(Helper.MAX_RETRY);		
			conf.setConnectionTimeout(Helper.TIMEOUT);
			h.s3Client = new AmazonS3Client(credentials,conf);
			h.setEndPoint();
			logger.debug("BucketS3 SINGLETON CREATED");
		} catch (AmazonServiceException e) {
			logger.error("ERROR BucketS3 "+e.getMessage());
			
		}  catch (RuntimeException e) {
			logger.error("ERROR BucketS3 "+e.getMessage());			
		}
		
		logger.info("BucketS3 READY");
	}
	
	/**
	 * 
	 * @return
	 */
	public static S3Bucket i() {
		synchronized (S3Bucket.class){ 
			if(instance == null){
				instance =  new S3Bucket();
			}
		}
		return instance; 
		
	}

	
	/*****************************************************************************************
	 * Helper of the S3Bucket Class
	 * @author Jean-Emmanuel
	 *****************************************************************************************/
	public class Helper{
		private String AWSAccessKeyId = "COLOCAR O SEU ID AQUI"; 
		private String AWSSecretKey = "COLOCAR O SEU SECRET KEY AQUI";

		public static final int TIMEOUT = 5000;
		private AmazonS3Client s3Client=new AmazonS3Client();
		public String provider = "S3";		
		public String endpoint = "https://s3-sa-east-1.amazonaws.com"; 
		private long TIMEOUT_URL = 24*1000*60*60; 
		private static final int MAX_RETRY=1;
		private static final String BUCKETNAME="fiscal65";
		
		public AmazonS3Client getS3(){
			return s3Client;
		}
		
		public void setEndPoint(){
			h.s3Client.setEndpoint(h.endpoint);
		}	
		
		/**
		 * calculate the expiration time
		 * @return Date containing expiration time
		 */
		private Date getExpirationDate(String httpVerb) {			
			Date expiration = new Date();
			long msec = expiration.getTime();			
			msec += TIMEOUT_URL;
			expiration.setTime(msec);
			return expiration;
		}	
	}
	
	public URL getSecuredURL(String KEY) {
		
		URL url = null;			
		//ERRORS
		if( KEY==null){
			return null;
		}
		
		try {											
			Date expiration = h.getExpirationDate("PUT");						
			GeneratePresignedUrlRequest g = new GeneratePresignedUrlRequest(Helper.BUCKETNAME, KEY )
													.withMethod(HttpMethod.PUT)
													.withExpiration(expiration);			
			url = h.s3Client.generatePresignedUrl(g);
		} catch (AmazonClientException e) {
			logger.error("BUCKET S3 ERROR in getURL PUT");	
			return null;
		}
		return url;
	}
	
	
	/**
	 * PUT document from FileInputStream to URL PUT
	 * 
	 * @param url_
	 * @param stream
	 * @return 201:OK , 400:ERROR
	 * 
	 */
	public int putDocument(URL url, InputStream stream){
		if (url == null || stream == null) {
			logger.error("malformed call!");
			return 400;
		}		
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			IOUtils.copy(stream, connection.getOutputStream());
			int responseCode = connection.getResponseCode();
			connection.disconnect();
			if (responseCode != 200)
				logger.error("!S3.putDocument :"+ responseCode+ "");
			else {
				logger.debug("+S3.putDocument OK : 201");
				return 201;
			}
			return responseCode;
		} catch (IOException e) {
			logger.error("BucketS3 File Write error");
			return 400;
		}

	}
	
	
	
	
	


}

