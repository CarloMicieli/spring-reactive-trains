import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    java
    jacoco
    checkstyle
    id("com.diffplug.spotless")
    id("io.spring.dependency-management")
    id("me.qoomon.git-versioning")
}

group = "io.carlomicieli"
version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
    refs {
        branch("main") {
            version = "\${commit.timestamp}-\${commit.short}"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }

    rev {
        version = "\${commit.short}-SNAPSHOT"
    }
}

repositories {
    mavenCentral()
}

configurations {
    all {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation {
        resolutionStrategy {
            failOnVersionConflict()
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    constraints {
        implementation("org.apache.logging.log4j:log4j-core") {
            version {
                strictly("[2.17, 3[")
                prefer("2.17.0")
            }
            because("CVE-2021-44228, CVE-2021-45046, CVE-2021-45105: Log4j vulnerable to remote code execution and other critical security vulnerabilities")
        }
    }

    annotationProcessor("io.soabase.record-builder:record-builder-processor:32")
    compileOnly("io.soabase.record-builder:record-builder-core:32")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.assertj:assertj-core")
}

tasks {
    withType<JavaCompile> {
        options.isIncremental = true
        options.isFork = true
        options.isFailOnError = false
    }

    test {
        useJUnitPlatform()

        maxHeapSize = "1G"
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

    jacocoTestReport {
        reports {
            xml.required.set(true)
            xml.outputLocation.set(file("${project.rootDir}/reports/jacoco/report.xml"))
            csv.required.set(false)
            html.required.set(false)
        }
    }

    tasks.withType<Checkstyle>().configureEach {
        configDirectory.set(file("${project.rootDir}/.config/checkstyle"))
        reports {
            xml.required.set(false)
            html.required.set(true)
        }
    }
}

checkstyle {
    configFile = file("${project.rootDir}/.config/checkstyle/checkstyle.xml")
    toolVersion = "9.3"
}

spotless {
    java {
        endWithNewline()
        removeUnusedImports()
        importOrder("java", "javax", "jakarta", "org.springframework")
        toggleOffOn("fmt:off", "fmt:on")
        indentWithSpaces()
        trimTrailingWhitespace()
        licenseHeaderFile("${project.rootDir}/.config/LICENSE")
        palantirJavaFormat("2.9.0")
    }

    kotlinGradle {
        endWithNewline()
        ktlint()
        indentWithSpaces()
        trimTrailingWhitespace()
    }

    tasks.check {
        dependsOn(tasks.spotlessCheck)
    }
}