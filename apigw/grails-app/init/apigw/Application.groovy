package apigw

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource


class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
	@Override
	void setEnvironment(Environment environment) {
		def configBase
		if (System.properties['os.name'].toLowerCase().contains('windows')) {
		    println "it's Windows"
		    println "C:\\opt\\APPGateWay\\application-${environment.activeProfiles[0]}.settings"
			configBase = new File("C:\\opt\\APPGateWay\\application-${environment.activeProfiles[0]}.settings")
		} else {
		    println "it's not Windows"
		    println "/opt/APPGateWay/application-${environment.activeProfiles[0]}.settings"
			configBase = new File("/opt/APPGateWay/application-${environment.activeProfiles[0]}.settings")
		}
		
		if(configBase.exists()) {
	    	println "Loading external configuration from Groovy: ${configBase.absolutePath}"
	    	println configBase
	    	def config = new ConfigSlurper().parse(configBase.toURL())
	    	println config
	    	environment.propertySources.addFirst(new MapPropertySource("externalGroovyConfig", config))
	   	} else {
			println "External config could not be found, checked ${configBase.absolutePath}"
	  	}
	}
}