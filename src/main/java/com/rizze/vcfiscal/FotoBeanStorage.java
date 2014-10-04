package com.rizze.vcfiscal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.rizze.vcfiscal.bean.FotosBean;
import com.rizze.vcfiscal.tools.S3Bucket;

public class FotoBeanStorage { 
	public Logger logger = Logger.getLogger(FotoBeanStorage.class);
	
	/**
	 * Enviar uma foto do objeto FotosBean para armazenar
	 * - envia a foto do android
	 * 
	 * Caution : please take a look at the bean.HTTP_CODE in order to know the status of the last action.
	 * 
	 * @param fotos lista completa
	 * @param numero da foto a enviar
	 * @param apagarDoAndroid true:apaga do android a foto, false: nao apaga do android
	 * @return enviostatus da foto
	 * 
	 */
	public FotosBean enviarFoto(FotosBean bean, int ordem){	
		//init http return code
		bean.HTTP_CODE=0;
		if(bean==null || bean.fotos==null){
			logger.error("enviarFoto in error");
			bean.HTTP_CODE=400;
			return bean;
		}
		else if (ordem<0 || ordem>bean.fotos.size() ){
			logger.error("enviarFoto ordem erro");
			bean.HTTP_CODE=400;
			return bean;
		}
		
		//FIND ORDEM ?
		int pos=bean.positionOrdem(ordem);
		if(pos==-1 || pos == bean.fotos.size()){			
			logger.error("enviarFoto ordem no foi encontrado!");
			bean.HTTP_CODE=400;
			return bean;
		}
		
		try {
			//IF PATH IMAGE IS NOT EMPTY
			if(bean.fotos.get(pos).photoPathAndroid!=null && bean.fotos.get(pos).photoPathAndroid.trim().length()!=0){
				logger.info("FOTO UPDATE");
				String KEY = bean.getPhotoKey(pos);
				URL url=S3Bucket.i().getSecuredURL(KEY);
				
				File fTemp = new File(bean.fotos.get(pos).photoPathAndroid);
				FileInputStream fIn= new FileInputStream(fTemp);
				
				int ret = S3Bucket.i().putDocument(url, fIn); //TODO PUT UIMAGE
				if(ret==400){ logger.error("S3Bucket error in write photo!"); bean.HTTP_CODE=400; return bean;}
				bean.fotos.get(pos).statusEnvioCode=201;
				bean.fotos.get(pos).statusEnvioTexto="ENVIADO PARA S3";	
				logger.info("FOTO UPDATE - DONE");
				
				
				logger.info("JSON UPDATE");
				String KEYJSON = bean.getJsonKey();
				url=S3Bucket.i().getSecuredURL(KEYJSON);
				String json =bean.toJson();
				ret= S3Bucket.i().putDocument(url, IOUtils.toInputStream(json,"UTF-8")); //TODO PUT JSON
				if(ret==400){ logger.error("S3Bucket error in write json!"); bean.HTTP_CODE=400; return bean;}
				bean.fotos.get(pos).statusEnvioTexto+=", JSON UPDATED";
				logger.info("JSON UPDATE - DONE");
			}	
			
			//ALL SUCCESSFUL
			bean.HTTP_CODE=201;
			return bean;
		} catch ( IOException e) {
			logger.error("ERROR Exception caught in enviarFoto!"+e.getMessage());
			bean.HTTP_CODE=400;
			return bean;
		}
	}
	
	

}
