import com.google.protobuf.gradle.id
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("java-library")
    id("com.google.protobuf") version "0.9.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
description = "axis-network ${project.version} gradle configurations"

val dubboCompiler: Configuration by configurations.creating

/* ⇢ dependencies */
dependencies {
    // expose dubbo
    api(libs.dubbo)
    // use default google rpc proto(s)
    api(libs.protobuf.java)
    // used for compiling fat dubbo jar
    dubboCompiler("org.apache.dubbo:dubbo-compiler:3.4.0.mutiny")
    dubboCompiler("com.github.spullara.mustache.java:compiler:0.9.14")
    dubboCompiler("io.grpc:grpc-core:1.72.0")
    dubboCompiler("io.grpc:grpc-stub:1.72.0")
    dubboCompiler("io.grpc:grpc-protobuf:1.72.0")
    // only compile mutiny
    compileOnly(libs.mutiny)
}

/* ⇢ build dubbo fat jar lib */
val dubboFat by tasks.registering(ShadowJar::class) {
    archiveClassifier.set("all") // generate *-all.jar file
    from(dubboCompiler.map {
        if (it.isDirectory) {
            it
        } else {
            zipTree(it)
        }
    })
    manifest {
        attributes["Main-Class"] = "org.apache.dubbo.gen.tri.mutiny.MutinyDubbo3TripleGenerator"
    }
}

/* ⇢ compile (dubbo:tri) rpc sources */
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }

    plugins {
        id("dubbo") {
            path = dubboFat.flatMap { it.archiveFile }.get().asFile.absolutePath
        }
    }

    generateProtoTasks {
        all().configureEach {
            plugins {
                id("dubbo")
            }
        }
    }
}