import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.netflix.dgs.codegen") version "6.1.6"
}

group = "app.alactive"
version = "0.1.0"

java { sourceCompatibility = JavaVersion.VERSION_17 }

repositories { mavenCentral() }

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // GraphQL | https://github.com/netflix/dgs-framework | Apache-2.0
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    // Server | https://github.com/spring-projects/spring-framework | Apache-2.0
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Security
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    testImplementation("org.springframework.security:spring-security-test")

    // Database
    implementation("org.neo4j:neo4j-ogm-core:4.0.11")
    implementation("org.neo4j:neo4j-ogm-bolt-driver:4.0.11")

    // FP | https://github.com/arrow-kt | Apache-2.0
    implementation("io.arrow-kt:arrow-core:1.2.4")
}

tasks.processResources {
    into("schema") { from("src/main/kotlin/app/alactive/api/schema") }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    generateJava {
        language = "kotlin"
        generateClient = true
        generateKotlinNullableClasses = true
        generateKotlinClosureProjections = true

        schemaPaths = mutableListOf("$projectDir/src/main/kotlin/app/alactive/api/schema")
        packageName = "app.alactive.data"

        typeMapping = mutableMapOf(
            "LocalDate" to "kotlinx.datetime.LocalDate",
            "Instant" to "kotlinx.datetime.Instant",
        )
    }
}
