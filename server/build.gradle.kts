import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.shadow)
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

group = "ru.ele638.mychatbot"
version = "1.0.0"
application {
    mainClass.set("ru.ele638.mychatbot.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.server.common)
}


tasks.register("deployBEtoEC2") {
    dependsOn(":server:build")
    doLast {
        val ec2Host = project.findProperty("ec2.host") as String
        val ec2User = project.findProperty("ec2.user") as String
        val keyPath = project.findProperty("ec2.keyPath") as String
        val deployDir = project.findProperty("ec2.deployDir") as String

        val jarPath = "${project(":server").layout.buildDirectory.get()}/libs/server-all.jar"

        val isWindows = OperatingSystem.current().isWindows

        val sshStopCmd = if (isWindows) {
            listOf("cmd", "/c", "plink", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl stop chatbot")
        } else {
            listOf("ssh", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl stop chatbot")
        }

        val scpCmd = if (isWindows) {
            listOf("cmd", "/c", "pscp", "-i", keyPath, jarPath, "$ec2User@$ec2Host:$deployDir/server.jar")
        } else {
            listOf("scp", "-i", keyPath, jarPath, "$ec2User@$ec2Host:$deployDir/server.jar")
        }

        val sshStartCmd = if (isWindows) {
            listOf("cmd", "/c", "plink", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl start chatbot")
        } else {
            listOf("ssh", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl start chatbot")
        }


        // Execute file transfers
        exec { commandLine(sshStopCmd) }
        exec { commandLine(scpCmd) }
        exec { commandLine(sshStartCmd) }

        println("Deployment BE to EC2 completed!")
    }
}

tasks.register("deployWASMToEC2") {
    dependsOn(":composeApp:wasmJsBrowserDistribution")

    doLast {
        val ec2Host = project.findProperty("ec2.host") as String
        val ec2User = project.findProperty("ec2.user") as String
        val keyPath = project.findProperty("ec2.keyPath") as String
        val wasmDir = project.findProperty("ec2.wasmDir") as String

        val wasmPath =
            "${project(":composeApp").layout.buildDirectory.get()}/dist/wasmJs/productionExecutable/"

        val isWindows = OperatingSystem.current().isWindows

        val sshStopCmd = if (isWindows) {
            listOf("cmd", "/c", "plink", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl stop nginx")
        } else {
            listOf("ssh", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl stop nginx")
        }

        val scpWasmCmd = if (isWindows) {
            listOf("cmd", "/c", "pscp", "-i", keyPath, "-r", wasmPath, "$ec2User@$ec2Host:$wasmDir")
        } else {
            listOf("scp", "-i", keyPath, "-r", wasmPath, "$ec2User@$ec2Host:$wasmDir")
        }

        val sshStartCmd = if (isWindows) {
            listOf("cmd", "/c", "plink", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl start nginx")
        } else {
            listOf("ssh", "-i", keyPath, "$ec2User@$ec2Host", "sudo systemctl start nginx")
        }


        // Execute file transfers
        exec { commandLine(sshStopCmd) }
        exec { commandLine(scpWasmCmd) }
        exec { commandLine(sshStartCmd) }

        println("Deployment WASM to EC2 completed!")
    }
}