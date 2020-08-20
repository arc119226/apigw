import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import java.nio.charset.Charset
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter
def HOME_DIR = ""
if (System.properties['os.name'].toLowerCase().contains('windows')) {
    HOME_DIR='C:\\opt\\APPGateWay'
} else {
    HOME_DIR='/opt/APPGateWay/'
}
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')
        pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}][%level][%t]%logger - %msg%n%wex"
    }
}
appender("ROLLING", RollingFileAppender) {
  encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')
        pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}][%level][%t]%logger - %msg%n%wex"


    }
  logger("StackTrace", INFO, ['ROLLING'], false)
  rollingPolicy(TimeBasedRollingPolicy) {
      fileNamePattern = "${HOME_DIR}/logs/%d{yyyy-MM-dd}.log"
      maxHistory = 10240
      totalSizeCap = FileSize.valueOf("100MB")
  }
}
def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
    root(ERROR, ['STDOUT'])
}else{
    root(INFO, ['ROLLING'])
}

