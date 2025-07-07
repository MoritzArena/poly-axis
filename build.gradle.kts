/* ⇢ gradle plugins */
plugins {
    id("java")
}

/* ⇢ global definitions */
val projectGroup: String = "io.polyaxis"
val projectVersion: String = "1.0.0-SNAPSHOT"
val slf4jVersion: String by project
val logbackVersion: String by project
val junit5Version: String by project
val mockitoVersion: String by project
val jakartaAnnotationVersion: String by project

/* ⇢ project definitions */
group = projectGroup
version = projectVersion
description = "Poly Axis's top gradle configuration."

/* ⇢ subprojects definitions */
allprojects {
    /* ⇢ gradle plugins */
    plugins.apply("java")

    /* ⇢ subproject definition */
    group = projectGroup
    version = projectVersion

    /* ⇢ maven repositories */
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }

    /* ⇢ dependencies */
    dependencies {
        /* logging */
        implementation("org.slf4j:slf4j-api:${slf4jVersion}")
        implementation("ch.qos.logback:logback-core:${logbackVersion}")
        implementation("ch.qos.logback:logback-classic:${logbackVersion}")
        /* annotation */
        implementation("jakarta.annotation:jakarta.annotation-api:${jakartaAnnotationVersion}")
        /* test */
        testImplementation(enforcedPlatform("org.junit:junit-bom:${junit5Version}"))
        testImplementation(enforcedPlatform("org.mockito:mockito-bom:${mockitoVersion}"))
    }

    /* ⇢ java version */
    java {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(24))
        }
    }

    /* ⇢ gradle tasks definitions */
    tasks {
        // test tasks configuration
        withType<Test> {
            systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
        }
        // keep name of parameters
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-parameters")
        }
    }
}

// disable pom jar building
tasks.withType<Jar> {
    enabled = false
}
