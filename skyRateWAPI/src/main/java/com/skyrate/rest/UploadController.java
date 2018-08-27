package com.skyrate.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skyrate.clib.util.UploadUtil;

@RestController
@RequestMapping("/upload")
@CrossOrigin( maxAge = 3600)
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50) 
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	


	 @RequestMapping(method = RequestMethod.POST, value="/uploader")
    @ResponseBody
    public String fileUploader(@RequestParam("file") MultipartFile uploader) throws Exception {
    		System.out.println("@@#@#@COming...");
    		String uploadedDir = uploadFile(uploader,UploadUtil.getBaseUploadDirectory());
      		return UploadUtil.USER_UPLOAD_WEB_ROOT+"/"+uploadedDir;
    		
    }
	 @RequestMapping(method = RequestMethod.POST, value="/productImageUploader")
	    @ResponseBody
	    public String productImageUploader(@RequestParam("file") MultipartFile uploader) throws Exception {
	    		System.out.println("@@#@#@Coming...productImageUploader");
	    		String uploadedDir = uploadFile(uploader,UploadUtil.getProductImageUploadDIR());
	      		return UploadUtil.PRODUCT_IMAGES_WEB_ROOT+"/"+uploadedDir;
	    		
	    }
	 @RequestMapping(method = RequestMethod.POST, value="/profileImageUploader")
	    public String profileImageUploader(@RequestParam("file") MultipartFile uploader) throws Exception {
	    		System.out.println("@@#@#@Coming...profileImageUploader");
	    		String uploadedDir = uploadFile(uploader,UploadUtil.getUserProfileImageUploadDIR());
	      		return UploadUtil.USER_PROFILE_IMAGES_WEB_ROOT+"/"+uploadedDir;
	    		
	    }	 
	
	 private String uploadFile( MultipartFile uploader, String uploadDIR) throws Exception{
	   		System.out.println("@@#@#@Coming...uploadFile.."+uploadDIR);
    		
    		String fileName = uploader.getOriginalFilename();
    		String todayDir="";
   
    		UploadUtil cd = new UploadUtil();
    		todayDir = cd.createDirectory(uploadDIR);
    		InputStream inputStream = uploader.getInputStream();	    		
			 
			File file = new File(uploadDIR+"/"+todayDir+"/"+fileName);
    		OutputStream outputStream = null;
    		
    		try{

    			// write the inputStream to a FileOutputStream
    			outputStream =
    	                    new FileOutputStream(file);

    			int read = 0;
    			byte[] bytes = new byte[1024];

    			while ((read = inputStream.read(bytes)) != -1) {
    				outputStream.write(bytes, 0, read);
    			}

    			System.out.println("Done! file uploaded to.."+file.getAbsolutePath());

    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			if (inputStream != null) {
    				try {
    					inputStream.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    			if (outputStream != null) {
    				try {
    					// outputStream.flush();
    					outputStream.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}

    			}
    		}
   			 File file2 = new File(file.getAbsolutePath());
   			 if(file2.exists()){
   				 file2.setExecutable(true,false);
   				 file2.setReadable(true,false);
   				 file2.setWritable(true,false);
   				 System.out.println("File world permission done..for "+file.getAbsolutePath());
   			 }
    	    

    		return todayDir+"/"+fileName;
	 }

	 
	 
	
	 
	
	    
}
