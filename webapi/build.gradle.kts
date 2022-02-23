import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    id("java-application-conventions")
}

dependencies {
    implementation(project(":catalog"))
    implementation(project(":collecting"))
    implementation(project(":infrastructure"))

    modules {
        module("org.springframework.boot:spring-boot-starter-tomcat") {
            replacedBy("org.springframework.boot:spring-boot-starter-reactor-netty", "Use Netty instead of Tomcat")
        }
    }

    implementation("com.auth0:java-jwt:3.18.3")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework:spring-jdbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project)

                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("io.projectreactor:reactor-test")
                implementation("org.springframework.security:spring-security-test")
                implementation("org.assertj:assertj-core")
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                        maxHeapSize = "512m"

                        failFast = false
                        ignoreFailures = true

                        testLogging {
                            showStandardStreams = false
                            events(PASSED, FAILED, SKIPPED)
                            showExceptions = true
                            showCauses = true
                            showStackTraces = true
                            exceptionFormat = FULL
                        }
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}