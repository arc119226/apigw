package apigw

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
@Secured(['permitAll'])
class ErrorController {

    def error500(){
    	def result=['500']
    	render result as JSON
    }

    def error404(){
    	def result=['404']
    	render result as JSON
    }

    def error403(){
    	def result=['403']
    	render result as JSON
    }
}
