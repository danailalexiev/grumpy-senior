plugins {
    id("java")
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "bg.dalexiev"
version = "0.0.1-SNAPSHOT"
description = "grumpy-senior"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val springAiVersion = "2.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.ai:spring-ai-starter-model-google-genai")

    implementation("io.github.pascalwilbrink.ag-ui.community:spring-ai:1.0.1")
    implementation("io.github.pascalwilbrink.ag-ui.community:spring:0.0.1")

    implementation("org.postgresql:postgresql:42.7.13")

    implementation("com.puppycrawl.tools:checkstyle:14.1.0")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    developmentOnly("org.springframework.ai:spring-ai-spring-boot-docker-compose")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${springAiVersion}")
    }
}

tasks.test {
    useJUnitPlatform()
}
