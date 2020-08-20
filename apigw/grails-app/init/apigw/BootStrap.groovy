package apigw

import org.springframework.beans.factory.annotation.Value
import com.arcare.security.*
class BootStrap {

    @Value('${info.app.version}')
    String appVersion

    def init = { servletContext ->
    	def user=User.findByUsername 'admin'
    	if(!user){
            initSecurity()
        }
        println "--- START ---"
        println "apigw Version: $appVersion"
    }

    def destroy = {
    }

    def initSecurity(){
    	def adminRole = Role.findOrSaveWhere(authority:'ROLE_ADMIN')
    	def devRole = Role.findOrSaveWhere(authority:'ROLE_DEV')
    	def userRole = Role.findOrSaveWhere(authority:'ROLE_USER')

    	def admin = User.findOrSaveWhere(username:'admin',password:'123456')
    	def dev = User.findOrSaveWhere(username:'arc',password:'123456')
    	def user = User.findOrSaveWhere(username:'user',password:'123456')

    	if(!admin.getAuthorities().contains(adminRole)){
    		UserRole.create(admin,adminRole,true)
    	}
    	if(!dev.getAuthorities().contains(devRole)){
    		UserRole.create(dev,devRole,true)
    	}
    	if(!user.getAuthorities().contains(userRole)){
    		UserRole.create(user,userRole,true)
    	}
    }
}
