grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.war.file = "target/ROOT.war"
grails.project.source.level = 1.6

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        runtime 'org.codehaus.gpars:gpars:0.10'
//        runtime 'org.coconut.forkjoin.jsr166y:jsr166y:070108'
    }
    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":jquery-ui:1.8.15"
        runtime ":quartz:0.4.2"

        // Resources
        runtime ":resources:1.1.6"
        runtime ":cache-headers:1.1.5"
        runtime ":lesscss-resources:1.0.1"
        runtime ":zipped-resources:1.0"
        runtime ":cached-resources:1.0"

        runtime ":spring-security-core:1.2.7.3"
        runtime ":spring-security-ldap:1.0.6"

        build ":tomcat:$grailsVersion"
        runtime ":database-migration:1.1"

        compile ':cache:1.0.0'
    }
}
