package apigw

class GwPrjRef {

	Integer id
	String prj_pn
	String comp_code
	String home_xml_pn
	String pr_code
	String prj_name
	String company_name
	GwSiteRef site

	static belongsTo = [site: GwSiteRef]

    static constraints = {
		"comp_code" nullable: false, blank: false,comment:'公司別代碼'
		"home_xml_pn" comment:'該專案, 該公司下的首頁XML 料號'
		"pr_code" comment:'Project 代碼'
		"prj_pn" nullable: false, blank: false , comment: '和SITE_ID 為唯一值; Project 料號'
		"prj_name" nullable: false, blank: false , comment: 'Project NAME'
		"company_name" nullable: false, blank: false , comment: 'Company NAME'
		site nullable: false,unique: ['prj_pn']
    }

    static mapping = {
    	id column: "gw_prj_reffid",name: "id"
        version false
    }
}
