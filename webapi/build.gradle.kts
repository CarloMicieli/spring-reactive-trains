import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("kotlin-application-conventions")
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
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
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
                implementation("io.kotest:kotest-assertions-core:5.1.0")
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

tasks.getByName<BootRun>("bootRun") {
    mainClass.set("io.github.carlomicieli.ApplicationKt")
}
