package apigw
import java.util.UUID
class GwSiteRef {

    Integer id
	String site_uuid = '{'+UUID.randomUUID().toString()+'}'
	String site_name
	String site_url

    static constraints = {
    	"site_uuid" nullable: false, blank: false,unique:true,comment:'全維碼, 為SITE 可辨識的唯一代碼. 設計者溝通時以此欄位為輔'
    	"site_name" nullable: false, blank: false,comment:'SITE SERVER 的名字'
    	"site_url" nullable: false, blank: false,comment:'SITE SERVER 完整的URL, 供連線至SERVER的API 使用'
    }

    static mapping = {
        id column: "site_id",name: "id"

        version false
    }

    String toString() {
        return this.id+':'+this.site_name+":"+this.site_url
    }

}
