package apigw

class GwFuncRef {

	Integer id	
	String prj_pn
	String comp_code
	String func_pn
	String func_name
	String xml_pn
    GwSiteRef site
    static belongsTo = [site: GwSiteRef]

    static constraints = {
    	"prj_pn" nullable: false, blank: false,comment:''
    	"comp_code" nullable: false, blank: false,comment:'公司別代碼'
    	"func_pn" nullable: false, blank: false,comment:'和SITE_ID為唯一值. 表單功能的參考鍏'
    	"func_name" nullable: false, blank: false,comment:'表單功能顯示的名字'
    	"xml_pn" nullable: false, blank: false,comment:'XML 料號'
        site nullable: false,unique: ['prj_pn']
    }

    static mapping = {
        id column: "gw_func_refid",name: "id"
        version false
    }
}
