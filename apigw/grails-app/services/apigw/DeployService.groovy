package apigw

import grails.gorm.transactions.Transactional
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import groovy.util.AntBuilder;
@Transactional
class DeployService {

    def doDeploy(String siteId,String file) {
		try {
			//1.unzip file
			//2.copy image file to /{ROOT}/img
			//3.parse XML
			//4.REMOVE OLD DATA
			//	save XML to GW_XML
			//  save data to GW_FUNC_REF,GW_PRJ_REF
			//5.fail roll back
			//6.log error message
		} catch (Exception e) {
			e.printStackTrace();
			log.error('doDeploy - '+e.message)
		}
		
    }

    private boolean importXML(GwSiteRef site,String xmlPath){

    }

    /**
    * 輸入檔案路徑 回傳base64字串
    * 回傳base64字串
    */
    def fileToBase64(String filePath){
    	try{
	    	byte[] bytes=Files.readAllBytes(Paths.get(filePath));
			String result=Base64.getEncoder().encodeToString(bytes);
			return result
		}catch(Exception e){
			e.printStackTrace()
    		log.error('fileToBase64 - '+e.message)
		}
		return null
    }

    /**
    * 輸入base64字串 轉為檔案
    * 回傳boolean true/false
    */
    def base64ToFile(String strBase64,String filePath){
    	boolean isSuccess=false;
    	try{
    		Files.write(Paths.get(filePath), Base64.getDecoder().decode(strBase64))
    		isSuccess=true
    	}catch(Exception e){
    		e.printStackTrace()
    		log.error('base64ToFile - '+e.message)
    	}
    	return isSuccess
    }

    def zip(String source,String targetFilePath){
    	boolean isSuccess=false
		try{
			def ant = new AntBuilder()
			ant.zip(destFile: "${targetFilePath}") {
			  fileset(dir: "${source}")
			}
			isSuccess=true
		}catch(Exception e){
			e.printStackTrace()
			log.error('zip - '+e.message)
		}
		return isSuccess
    }

    def unzip(String source,String outputDir){
    	boolean isSuccess=false
		try{
			def ant = new AntBuilder()
			ant.unzip(src: "${source}", dest:"${outputDir}");
			isSuccess=true
		}catch(Exception e){
			e.printStackTrace()
			log.error('unzip - '+e.message)
		}
		return isSuccess
	}
}
