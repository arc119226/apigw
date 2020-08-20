package apigw

class GwXml {

	Integer id
	String xml_pn
	String xml_name
	String lang_code
	String xml_content
	Date last_update = new Date()
    GwSiteRef site
    static belongsTo = [site: GwSiteRef]

    static constraints = {
    	"xml_pn" nullable: false, blank: false,comment:'XML 料號. 和SITE_ID 為唯一值'
    	"xml_name" comment:'XML的名字'
    	"lang_code" nullable: false, blank: false,maxSize:10,comment:'語言代碼'
    	"xml_content" nullable: false, blank: false,maxSize:15000,comment:'XML的內容'
    	"last_update" comment:'最後更新時間'
        site nullable: false,unique: ['xml_pn']
    }
    static mapping = {
         id column: "xml_id",name: "id"
        version false
    }
}
