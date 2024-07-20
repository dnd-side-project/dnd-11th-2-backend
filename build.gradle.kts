plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.dnd"
version = "0.0.1-SNAPSHOT"

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
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    createdDate = "now"
}

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
        formatAnnotations()
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
}

tasks.register<Exec>("makeGitHooksExecutable") {
    commandLine("chmod", "+x", ".git/hooks/pre-commit")
    dependsOn("updateGitHooks")
}

tasks.compileJava {
    dependsOn("makeGitHooksExecutable")
}
