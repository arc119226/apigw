package apigw

class LangCode {

	String localeCode
	Integer langCode

    static constraints = {
    	"localeCode" nullable: false, blank: false
    	"langCode" nullable: false, blank: false
    }

    static mapping = {
        version false
    }
}
