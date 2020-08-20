package apigw
import java.util.UUID
class GwRegist {

	String gw_regist_id = '{'+UUID.randomUUID().toString()+'}'
	String regist_uuid
	String regist_os
	Date regist_date = new Date()

    static constraints = {
    	"gw_regist_id" nullable: false, blank: false, unique: true,comment: 'GUID'
    	"regist_uuid" nullable: false, blank: false, unique: true,comment: '手機的UUID , ANDROID 是MAC ADDRESS, IPHONE 是UUID'
    	"regist_os" nullable: false, blank: false,maxSize:10,comment:'手機OS 類型'
    	"regist_date" nullable: false, blank: false,comment:'註冊的時間'
    }

    static mapping = {
        id generator:'assigned', name: 'gw_regist_id'

        version false
    }
}
