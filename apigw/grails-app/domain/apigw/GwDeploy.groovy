package apigw

class GwDeploy {
	Integer id
	String file_name
	String version_code
	Date create_date = new Date()
	Boolean deploy_success
    GwSiteRef site
    static belongsTo = [site: GwSiteRef]

    static constraints = {
    	"file_name" nullable: false, blank: false,comment:'發行的檔案名稱'
    	"version_code" nullable: false, blank: false,maxSize:50,comment:'發行的版本號(在發行的XML 內)'
    	"create_date" nullable: false, blank: false,comment:'寫入資料的時間'
    	"deploy_success" nullable: false, blank: false,comment:'是否成功發行'
    }

    static mapping = {
        id column: "gw_deploy_id",name: "id"
        version false
    }
}
