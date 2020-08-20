package apigw

import grails.gorm.transactions.Transactional
import groovyx.net.http.RESTClient
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
@Transactional
class GatewayService {
	//cookie
	static String csrf='{AAAA1310-83C6-44A3-A6AF-3329F5B44EEE}'

    /**
	* log client uuid and os
    */
    def registGW(String uuid,String os){
    	try{
	    	def gwRegist = GwRegist.findOrSaveWhere(regist_uuid:uuid,regist_os:os)
			gwRegist.regist_date = new Date()
			if (!gwRegist.save(flush: true)) {
			    gwRegist.errors.allErrors.each {
			        log.error('registGW - '+it)
			    }
			}
		}catch(Exception e){
			log.error('registGW - '+e.message)
		}
    }

    /**
    * find all function list
    */
    def funcList(String file){
    	def responseData=[]
    	try{
	    	if(file && file=='asksys'){
		    	GwPrjRef.findAll().each{
						responseData.add([prjPn: it.prj_pn,siteId: it.site.id,prjName: it.prj_name])
					}
			}else{
				log.error('funcList - file not equal asksys')
			}
		}catch(Exception e){
			log.error('funcList - '+e.message)
		}
		return responseData
    }

    /**
    * find all site list
    */
    def gwSiteAPI(String file){
		def responseData = []
		try{
			if(file && file=='asksite'){
				GwSiteRef.findAll().each{
					responseData.add([strSiteName: it.site_name,strSiteUUID: it.site_uuid])
				}
			}else{
				log.error('gwSiteAPI - file not equal asksite')
			}
		}catch(Exception e){
			log.error('gwSiteAPI - '+e.message)
		}
		return responseData
    }

    /**
    * get xml content
    */
    def getXML(String site,String lang,String xml_file){
		def responseData = []
		try{
			if(!site || !lang || !xml_file){//check post value
				log.error('getXML - site:'+site)
				log.error('getXML - lang'+lang)
				log.error('getXML - xml_file:'+xml_file)
				return responseData
			}
			def obj = GwSiteRef.findById(site)
			if(!obj){//check site obj
				log.error('getXML - GwSiteRef.findById:'+site)
				return responseData
			}
			def xml = GwXml.findWhere(site:obj,lang_code:lang,xml_name:xml_file)
			if(!xml){//check xml obj
				log.error('getXML - GwXml.findWhere.site:'+obj)
				log.error('getXML - GwXml.findWhere.lang_code:'+lang)
				log.error('getXML - GwXml.findWhere.xml_name:'+xml_file)
				return responseData
			}
			responseData = [file:xml.xml_content]
		}catch(Exception e){
			log.error('getXML: '+e.message)
		}
		return responseData
    }

    def funcAuthList(String site,String usrid,String prjCode,String compCode,String langCode){
    	def responseData = []
    	try{
    		if(!site || !usrid || !prjCode || !compCode || !langCode){//check post value
				log.error('funcAuthList - site:'+site)
				log.error('funcAuthList - usrid:'+usrid)
				log.error('funcAuthList - prjCode:'+prjCode)
				log.error('funcAuthList - compCode:'+compCode)
				log.error('funcAuthList - langCode:'+langCode)
				return responseData
			}
	    	def obj = GwSiteRef.findById(site)
			if(!obj){//check site
				log.error('funcAuthList - GwSiteRef.findById:'+site)
				return responseData
			}
			
			def langObj=LangCode.findByLocal(langCode)
			def _langCode=950//default
			if(langObj){
				_langCode=langObj.langCode
			}else{
				log.info('funcAuthList - use default langCode:'+_langCode)
			}

			RESTClient rest=new RESTClient("${obj.site_url}")
	    	def resp = rest.post(
	    		path:'ArcareEng/AppRuntimeGetAuth',
	    		body: [nUserId:usrid,
	    			   strProjectId:prjCode,
	    			   strCompanyId:compCode,
	    			   strLanguageId:_langCode,
	    			   strCSRF:csrf],
	    		requestContentType: 'application/json'
	    	)

	    	if(resp.status != 200){//check http status
	    		log.error('funcAuthList - check http status:'+http.status)
				return responseData
	    	}

			if(!resp.data.bSuccess){//check success
				responseData=resp.data
	    		return responseData
	    	}

	    	resp.data.arrFunctionList.each{
	    		def _funcId=it
	    		def gwFuncRefObj=GwFuncRef.findBySiteAndFunc_pn(site,_funcId)
	    		if(gwFuncRefObj){
	    			responseData.add([funcID:_funcId,
	    							  funcName:gwFuncRefObj.func_name,
	    							  xmlPn:gwFuncRefObj.xml_pn])
	    		}else{
	    			log.info('funcAuthList - '+_funcId+' this funcId not in system.');
	    		}
	    	}
    	}catch(Exception e){
    		log.error('funcAuthList - '+e.message)
    	}
    	return responseData;
    }

    def lgnAPI(String site,String uid,String pwd,String prj){
    	def responseData = []
		try{
			if(!site || !uid || !pwd || !prj){//check post value
				log.error('lgnAPI - lgnAPI:'+site)
				log.error('lgnAPI - uid:'+uid)
				log.error('lgnAPI - pwd:'+pwd)
				log.error('lgnAPI - prj:'+prj)
				return responseData
			}

			def obj = GwSiteRef.findById(site)
			if(!obj){//check site
				log.error('lgnAPI - GwSiteRef.findById:'+site)
				return responseData
			}

			RESTClient rest=new RESTClient("${obj.site_url}")
	    	def resp = rest.post(
	    		path:'ArcareEng/ProjectQueryService',
	    		headers:['Set-Cookie':'csrf='+csrf],
	    		body: [strAccount:uid,
	    			   strPassword:pwd,
	    			   strProjectNo:prj,
	    			   strCSRF:csrf],
	    		requestContentType: 'application/json'
	    	)

	    	if(resp.status != 200){//check http status
	    		log.error('lgnAPI - check http status:'+http.status)
				return responseData
	    	}

			if(!resp.data.bSuccess){//check success
				responseData=resp.data
	    		return responseData
	    	}

	    	def _result = []
	    	resp.data.arrCompanyList.each{
	    		def _corpCode=it.strCompanyId
	    		def _corpName=it.strCompanyName
	    		_result.add([corpCode:_corpCode,corpName:_corpName])
	    	}

			responseData=[LASTPK:resp.data.nUserId,
						  prjcode:resp.data.strProjectId,
						  homPage:resp.data.strHomePageNo,
						  result:_result]
		}catch(Exception e){
			log.error('lgnAPI - '+e.message)
		}
    	return responseData
    }

    def qyECONEW(String site,String data){
    	def responseData = []
    	try{
    		if(!site || !data){//check post data
    			log.error('qyECONEW - site:'+site)
    			log.error('qyECONEW - data:'+data)
				return responseData
			}
			def obj = GwSiteRef.findById(site)
			if(!obj){//check site obj
				log.error('qyECONEW - GwSiteRef.findById:'+site)
				return responseData
			}

			// 1. 依site ID 取得對應server, 其他不變
			RESTClient rest=new RESTClient("${obj.site_url}")
	    	def resp = rest.post(
	    		path:'ArcareEng/ProjectQueryService',
	    		body: [strProjectId:data.pjcode,
	    			   strCompanyId:data.cocode,
	    			   strApiId:data.table,
	    			   oParamValue:data.content],
	    		requestContentType: 'application/json'
	    	)

	    	if(resp.status != 200){//check http status
	    		log.error('qyECONEW - check http status:'+http.status)
				return responseData
	    	}

	    	if(!resp.data.bSuccess){//check success
	    		return responseData
	    	}
	    	responseData=resp.data.record
    	}catch(Exception e){
    		log.error('qyECONEW - '+e.message)
    	}
    	return responseData
    }

    def svEcoNew(String site,String data){
    	def responseData = []
    	try{
			if(!site || !data){//check value
				responseData=[code:0,msg:'post value empty.']
				return responseData
			}

			def obj = GwSiteRef.findById(site)
			if(!obj){//check site obj
				log.error('svEcoNew - GwSiteRef.findById:'+site)
				responseData=[code:0,msg:'site id not set in api gateway.']
				return responseData
			}

			// 1. 依site ID 取得對應server, 其他不變 
			// 2. BASE64碼需將" " 替換成"+"
			// 3. base64 碼的欄位名在arrBinaryParam 內的JSON Array 內
			def dataJsonObj = new JsonSlurper().parseText(data)
			def mapResult = dataJsonObj.arrBinaryParam.collect{str->
				str=str.replaceAll("\\ ","+").replaceAll("\\r|\\n", "")
			}

			object.arrBinaryParam=mapResult
			def parseBase64Data = new JsonOutput().toJson(object)

			RESTClient rest=new RESTClient("${obj.site_url}")
	    	def resp = rest.post(
	    		path:'ArcareEng/ProjectPsdService',
	    		body: parseBase64Data,
	    		requestContentType: 'application/json')

	    	if(resp.status != 200){//check http status
	    		log.error('svEcoNew - api gateway connection error. http status:'+resp.status)
	    		responseData=[code:0,msg:'api gateway connection error. http status:'+resp.status]
	    		return responseData
	    	}

	    	if(resp.data.bSuccess){//check api result
	    		responseData=[code:1,msg:resp.data.result]
	    	}else{
	    		responseData=[code:0,msg:resp.data.result]
	    	}
    	}catch(Exception e){
    		log.error('svEcoNew - '+e)
    	}
    	return responseData
    }
}
