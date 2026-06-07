plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.epages.restdocs-api-spec") version "0.19.4"
    id("org.sonarqube") version "5.1.0.4882"
}

group = "com"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["snippetsDir"] = file("build/generated-snippets")
val springAiVersion by extra("1.0.1")
val springCloudVersion by extra("2023.0.6")

dependencies {
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.hibernate.orm:hibernate-vector:6.6.15.Final")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.google.firebase:firebase-admin:9.5.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.pgvector:pgvector:0.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.google.code.gson:gson:2.10.1")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.pgvector:pgvector:0.1.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)

    attributes(mapOf("snippets" to project.extra["snippetsDir"]!!))

    doFirst {
        delete(file("src/main/resources/static/docs"))
    }
}

tasks.register("copyRestdocsToStatic") {
    group = "documentation"
    description = "Copy generated REST Docs to static resources for OpenAPI JSON generation"
    dependsOn(tasks.asciidoctor)

    doLast {
        copy {
            from(file("build/docs/asciidoc"))
            into(file("src/main/resources/static/docs"))
        }
    }
}

openapi3 {
    setServer("http://localhost:8080")
    title = "시점 API"
    version = "1.0.0"
    format = "yaml"
}

tasks.register("generateOpenApiSpec") {
    group = "documentation"
    description = "Generate OpenAPI specification from REST Docs tests"
    dependsOn(tasks.test, tasks.named("openapi3"))
}

tasks.register("prepareDocsForGithubPages") {
    group = "documentation"
    description = "Prepare documentation for GitHub Pages deployment"
    dependsOn(tasks.asciidoctor, tasks.named("generateOpenApiSpec"))

    doLast {
        copy {
            from(file("build/docs/asciidoc"))
            into(file("docs"))
        }
        if (file("build/api-spec").exists()) {
            copy {
                from(file("build/api-spec"))
                into(file("docs/api-spec"))
            }
        }
    }
}

tasks.bootJar {
    archiveBaseName.set(rootProject.name)
    archiveVersion.set("")
}

sonar {
    properties {
        property("sonar.projectKey", "redstone-swm_flownews")
        property("sonar.organization", "redstone-swm")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.qualitygate.wait", "true")
    }
}
