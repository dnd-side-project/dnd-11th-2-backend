plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.dnd"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.3")

    // Lombok
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // testcontainers
    testImplementation("org.testcontainers:testcontainers:1.20.0")
    testImplementation("org.testcontainers:junit-jupiter:1.20.0")
    testImplementation("org.testcontainers:jdbc:1.20.0")
    testImplementation("org.testcontainers:postgresql:1.20.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    createdDate = "now"
    doFirst {
        version = imageName.get().substringAfterLast(":", "Unknown")
        println("Setting version to: $version")
    }
}

springBoot.buildInfo { properties {} }

spotless {
    encoding = Charsets.UTF_8

    java {
        target("**/*.java")
        targetExclude("build/**", "**/*Request*.java", "**/*Response*.java", "**/*Dto*.java")
        palantirJavaFormat("2.47.0")
    }

    java {
        target("**/*.java")
        endWithNewline()
        indentWithSpaces(4)
        trimTrailingWhitespace()
        importOrder("", "java|javax", "\\#").wildcardsLast()
        removeUnusedImports()
    }

    format("misc") {
        // define the files to apply `misc` to
        target("*.gradle", "*.gradle.*", ".gitattributes", ".gitignore")

        // define the steps to apply to those files
        endWithNewline()
        indentWithSpaces(4)
        trimTrailingWhitespace()
    }
}

tasks.register<Copy>("updateGitHooks") {
    from("scripts/pre-commit.sh")
    into(".git/hooks")
    rename("pre-commit.sh", "pre-commit")
    doLast {
        val preCommitHook = file(".git/hooks/pre-commit")
        preCommitHook.setExecutable(true, false)
    }
}

tasks.compileJava {
    dependsOn("updateGitHooks")
}

tasks.register("projectTest") {
    dependsOn("spotlessJavaCheck")
    dependsOn("compileJava")
    dependsOn("test")
}
