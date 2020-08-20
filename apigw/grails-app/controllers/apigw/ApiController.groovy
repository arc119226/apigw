package apigw

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import grails.core.GrailsApplication
@Secured(['permitAll'])
class ApiController {

    static allowedMethods = [
	            registGW:['POST'],
	            funcList:['POST'],
	            funcAuthList:['POST'],
	            lgnAPI:['POST'],
	            qyECONEW:['POST'],
	            svEcoNew:['POST'],
	            getXML:['POST'],
	            gwSiteAPI:['POST'],
				doDeploy:['POST']
    		]

    GatewayService gatewayService
    DeployService deployService
    GrailsApplication grailsApplication

    def index(){
    	def result=['ok']
    	render result as JSON
    }


    def infoFromProxy(){

    	def result = 
    		[serverName:"${request.getServerName()}",
    		 serverPort:"${request.getServerPort()}",
    		 localPort:"${request.getLocalPort()}",
    		 frontUrl:"${request.requestURL}",
    		 remoteAddr:"${request.getRemoteAddr()}",
    		 xForwardedFor:"${request.getHeader("X-Forwarded-For")}",
    		 xRealIp:"${request.getHeader("X-Real-IP")}",
    		 xRealPort:"${request.getHeader("X-Real-Port")}"]
    	render result as JSON
    }

    //Logic
    //1.不管資料寫入是否完成, 一律回error =0
	//2. 資料寫入registGW
	//Purpose
	//APP 設定gateway時, 驗證並留下手機資訊
	//curl -i -d 'data={"uuid":"value111","os":"value222"}' -X POST https://ip:port/api/registGW
    //output example:
    //{"error":0,msg:''}
    def registGW(){
		String uuid 
		String os
		def responseData = [error:0,msg:'']
		if(request.getHeader('content-type') != 'application/x-www-form-urlencoded'){
			responseData = [error:0,msg:'contentType error']
			render responseData as JSON
			return
		}
		String data = params.data
		if(!data){
			responseData = [error:0,msg:'data is empty']
			render responseData as JSON
			return
		}

		try{
			def dataJsonObj = new JsonSlurper().parseText(data)
			uuid = dataJsonObj.uuid
			os = dataJsonObj.os
		}catch(Exception e){
			responseData = [error:0,msg:'data not a valid json format.']
			render responseData as JSON
			return
		}
		if(!uuid || !os){//check post value
			responseData = [error:0,msg:'post value empty']
			render responseData as JSON
			return
		}

    	render responseData as JSON
    	//async thread
    	gatewayService.registGW(uuid,os)

    }

    //回傳所有prj pn, prj_name 及存在的site_id
    //取得所有系統清單
    //curl -i -d "data=asksys" -X POST http://ip:port/api/funcList
	// outout example:
	// [{
	// "prjPn": "Prjpn 001",
	// "siteId": 1,
	// "prjName": "Prjname 001"
	// }, {
	// "prjPn": "Prjpn 002",
	// "siteId": 2,
	// "prjName": "Prjname 002"
	// }, {
	// "prjPn": "Prjpn 003",
	// "siteId": 3,
	// "prjName": "Prjname 003"
	// }]
    def funcList(){
		def responseData=[]
		if(request.getHeader('content-type') != 'application/x-www-form-urlencoded'){
			log.error('funcList - content-type enot vaild.')
    		render responseData as JSON
    		return
		}
		String file  = params.data
		responseData = gatewayService.funcList(file)
    	render responseData as JSON
    }

    //curl -i -d "site=value1&usrid=value2&prjCode=value3&compCode=value4" -X POST http://ip:port/api/funcAuthList
    //curl -i -d '{"site":"value1", "usrid":"value2","prjCode":"value3","compCode":"value4"}' -H "Content-Type: application/json" -X POST  http://ip:port/api/funcAuthList
    def funcAuthList(){
		String site
		String usrid
		String prjCode
		String compCode 
		String langCode
		if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		site = params.site
    		usrid = params.usrid
    		prjCode = params.prjCode
    		compCode = params.compCode
    		langCode = params.langCode
		}else if(request.getHeader('content-type') == 'application/json'){
			def jsonObject = request.JSON
			site = jsonObject.site
    		usrid = jsonObject.usrid
    		prjCode = jsonObject.prjCode
    		compCode = jsonObject.compCode
    		langCode = jsonObject.langCode
		}
		def responseData = gatewayService.funcAuthList(site,usrid,prjCode,compCode,langCode)
    	render responseData as JSON
    }

	//curl -i -d "site=value1&uid=value2&pwd=value3&prj=value4" -X POST http://ip:port/api/lgnAPI
	//curl -i -d '{"site":"value1", "uid":"value2","pwd":"value3","prj":"value4"}' -H "Content-Type: application/json" -X POST  http://ip:port/api/lgnAPI
    def lgnAPI(){
		String site
		String uid
		String pwd
		String prj 
		if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		site = params.site
    		uid = params.uid
    		pwd = params.pwd
    		prj = params.prj
		}else if(request.getHeader('content-type') == 'application/json'){
			def jsonObject = request.JSON
			site = jsonObject.site
    		uid = jsonObject.uid
    		pwd = jsonObject.pwd
    		prj = jsonObject.prj
		}
		def responseData = gatewayService.lgnAPI(site,uid,pwd,prj)
    	render responseData as JSON
    }

    def qyECONEW(){
    	String site
    	String data
    	if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		site = params.site
    		data = params.data
    	}
    	def responseData = gatewayService.qyECONEW(site,data)
    	render responseData as JSON //[]  = API 回傳之record:[]
    }

    def svEcoNew(){
    	String site
    	String data
    	if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		site = params.site
    		data = params.data
    	}
		def responseData = gatewayService.svEcoNew(site,data)
    	render responseData as JSON
    }

    //curl -i -d "site=1&lang=zh-tw&xml_file=Xmlname01" -X POST http://ip:port/api/getXML
    //curl -i -d '{"site":"1", "lang":"zh-tw","xml_file":"Xmlname01"}' -H "Content-Type: application/json" -X POST http://ip:port/api/getXML
    def getXML(){
    	String site
		String lang
		String xml_file
		if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		site = params.site
    		lang = params.lang
    		xml_file = params.xml_file
		}else if(request.getHeader('content-type') == 'application/json'){
			def jsonObject = request.JSON
    		site = jsonObject.site
    		lang = jsonObject.lang
    		xml_file = jsonObject.xml_file
		}
		def responseData = gatewayService.getXML(site,lang,xml_file)
		render responseData as JSON	
    }

	//curl -i -d "data=asksite" -X POST http://ip:port/api/gwSiteAPI
    def gwSiteAPI(){
		String file 
		if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		file = params.file
		}else if(request.getHeader('content-type') == 'application/json'){
			def jsonObject = request.JSON
			file = jsonObject.file
		}
		def responseData = gatewayService.gwSiteAPI(file)
    	render responseData as JSON
    }

    //curl -i -d "siteId=xxxx_uuid&file=xxxx.zip" -X POST http://127.0.0.1:8080/api/doDeploy
    def doDeploy(){
    	String siteId
    	String file //base64
    	if(request.getHeader('content-type') == 'application/x-www-form-urlencoded'){
    		siteId = params.siteId
    		file = params.file
		}else if(request.getHeader('content-type') == 'application/json'){
			def jsonObject = request.JSON
			siteId = jsonObject.siteId
			file = jsonObject.file
		}
		deployService.doDeploy(siteId,file)
    	//siteId(uuid), file
    	def responseData=[error:0,msg:'',,strFileName:'',intFileSize:0]
    	render responseData as JSON
    }

}
