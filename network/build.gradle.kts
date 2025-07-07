import com.google.protobuf.gradle.id
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("java-library")
    id("com.google.protobuf") version "0.9.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

/* ⇢ definitions */
description = "axis-network ${project.version} gradle configurations"

val dubboCompiler: Configuration by configurations.creating

/* ⇢ dependencies */
dependencies {
    // expose dubbo
    api(libs.dubbo)
    // used for compiling fat dubbo jar
    dubboCompiler("org.apache.dubbo:dubbo-compiler:3.3.4")
    dubboCompiler("com.github.spullara.mustache.java:compiler:0.9.14")
    dubboCompiler("io.grpc:grpc-core:1.72.0")
    dubboCompiler("io.grpc:grpc-stub:1.72.0")
    dubboCompiler("io.grpc:grpc-protobuf:1.72.0")
    // use default google rpc proto(s)
    implementation(libs.protobuf.java)
}

/* ⇢ build dubbo fat jar lib */
val dubboFat by tasks.registering(ShadowJar::class) {
    archiveClassifier.set("all") // generate *-all.jar file
    from(dubboCompiler.map {
        if (it.isDirectory) it
        else zipTree(it)
    })
    manifest {
        attributes["Main-Class"] = "org.apache.dubbo.gen.tri.Dubbo3TripleGenerator"
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