package com.rizze.vcfiscal;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.rizze.vcfiscal.bean.FotosBean;

public class FotosBeanTest {
	public Logger logger = Logger.getLogger(FotosBeanTest.class);

	@Test
	public void test_create() {
		FotosBean fb= new FotosBean();
		assertTrue(fb.data!=null);
	}
	

	@Test
	public void test_create_add() {
		FotosBean fb= new FotosBean();		
		assertTrue(fb.setFiscal("toto", null, "toto@uol.com.br", "nao sei!")==200);		
		assertTrue(fb.setFiscal(null, null, null, null)==200);
		
		assertTrue(fb.setLugar("MA", "CAXIAS", "1243455", "ESCOLA CHICO BUARQUE", "URNA-12455")==200);
		assertTrue(fb.addFoto("c:\\foto1.jpg", 1)==200);
		
		//in error ordem in double!
		assertTrue(fb.addFoto("c:\\foto2.jpg", 1)==400);
		assertTrue(fb.addFoto("c:\\foto3.jpg", 3)!=400);
		assertTrue(fb.addFoto("c:\\foto2.jpg", 2)!=400);
		assertTrue(fb.positionOrdem(3)==1);
		assertTrue(fb.positionOrdem(1)==0);
		assertTrue(fb.positionOrdem(2)==2);
		
	}
	
	@Test
	public void test_setLugarTest() {		
		FotosBean fb= getFotoBean();	
		assertTrue(fb.setLugar("MA", "CAXIAS", "1243455", "ESCOLA CHICO BUARQUE", "URNA-12455")==200);		
		assertTrue(fb.setLugar(null, "CAXIAS", "1243455", "ESCOLA CHICO BUARQUE", "URNA-12455")==400);
		assertTrue(fb.setLugar("MA", null, "1243455", "ESCOLA CHICO BUARQUE", "URNA-12455")==400);		
	}
	
	
	@Test
	public void addFotoTest() {
		
		FotosBean fb=  getFotoBean();	
		
		//in error ordem in double!
		assertTrue(fb.addFoto("d:\\test\\success2.jpg", 1) !=400);
		assertTrue(fb.addFoto("d:\\test\\success3.jpg", 1) ==400);
		assertTrue(fb.addFoto("d:\\test\\success2.jpg", 3)!=400);
		assertTrue(fb.addFoto("d:\\test\\success3.jpg", 2)!=400);
		
		
	}
	
	
	
	
	@Test
	public void testKey__Test() {
		
		FotosBean fb= getFotoBeanFull();
		assertTrue(fb.getJsonKey()!=null);
		assertTrue(fb.getJsonKey().trim().length()!=0);
		assertTrue(fb.getJsonKey().compareTo(fb.getJsonKey())==0);
		
	}
	
	
	private FotosBean getFotoBeanFull() {
		FotosBean fb= new FotosBean();	
		fb.setLugar("MA", "CAXIAS", "1243455", "ESCOLA CHICO BUARQUE", "URNA-12455");
		fb.setFiscal("Jean", "Jehan", "juanito@jean.jehan.com.br", "jeanzinho");	
		assertTrue(fb.addFoto("d:\\test\\success1.jpg", 1) !=400);
		assertTrue(fb.addFoto("d:\\test\\success3.jpg", 3)!=400);
		assertTrue(fb.addFoto("d:\\test\\success2.jpg", 2)!=400);
		return fb;
	}	
	
	//@Test
	public void storgeTest(){
		FotosBean fb=getFotoBeanFull();
		FotoBeanStorage fbs= new FotoBeanStorage();
		fb=fbs.enviarFoto(fb, 3);
	//	junitTestReturn(fb);
		fb=fbs.enviarFoto(fb, 1);
	//	junitTestReturn(fb);
		fb=fbs.enviarFoto(fb, 2);
		junitTestReturn(fb);
		
	}


	private void junitTestReturn(FotosBean fb) {
		if(fb.HTTP_CODE==201)logger.info("+3 done");
		else logger.error("put not performed!");
		
		assertTrue(fb.HTTP_CODE==201 || fb.HTTP_CODE==200);
	}
	
	
	/************************** TESTS TOOLS ***********************************/
	
	private FotosBean getFotoBean() {
		FotosBean fb= new FotosBean();	
		fb.setLugar("MA", "CAXIAS", "1243455", "ESCOLA CHICO BUARQUE", "URNA-12455");
		fb.setFiscal("Jean", "Jehan", "juanito@jean.jehan.com.br", "jeanzinho");
		return fb;
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
