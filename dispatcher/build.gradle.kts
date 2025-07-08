import io.quarkus.gradle.tasks.QuarkusDev
import org.gradle.kotlin.dsl.named

plugins {
    id("java")
    id("io.quarkus")
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://packages.aliyun.com/65442f8f3aaed849b035c821/maven/repo-dubbo-mutiny")
        credentials {
            username = "63c4e472b7bea95c53d1625e"
            password = "SJFkqm-_akW_"
        }
    }
}

/* ⇢ definitions */
description = "axis-dispatcher ${project.version} gradle configurations"

dependencies {
    /* quarkus bom */
    implementation(enforcedPlatform(libs.quarkus))
    /* quarkus reactive web */
    implementation(libs.quarkus.reactive.routes)
    /* quarkus langchain4j */
    implementation(libs.quarkus.langchain4j.core)
    implementation(libs.quarkus.langchain4j.openai)
    /* internal projects */
    implementation(project(":common"))
    implementation(project(":network")) // for rpc negotiating
}

/* ⇢ add opens */
//tasks.quarkusDev {
//    jvmArgs.add("--add-opens=java.base/java.lang=ALL-UNNAMED")
//}