package com.prof18.kmp.fatframework.cocoa.task.xcframework.common

import com.prof18.kmp.fatframework.cocoa.data.PluginConfig
import com.prof18.kmp.fatframework.cocoa.utils.dsymFile
import com.prof18.kmp.fatframework.cocoa.utils.execBashCommandInRepoAndThrowExecException
import com.prof18.kmp.fatframework.cocoa.utils.executeBashCommand
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import java.io.File

internal fun Exec.buildXCFramework(config: PluginConfig, frameworks: List<Framework>) {
    for (framework in frameworks) {
        dependsOn(framework.linkTaskName)
    }

    val xcFrameworkDest = File("${config.outputPath}/${config.frameworkName}.xcframework")

    try {
        project.executeBashCommand(
            showOutput = true,
            workingDirPath = config.outputPath,
            commandList = listOf("ls")
        )
        project.executeBashCommand(
            showOutput = true,
            workingDirPath = config.outputPath,
            commandList = listOf("rm", "-rf", "${config.frameworkName}.xcframework")
        )
    } catch (error: Exception) {
        project.logger.log(LogLevel.WARN, "Failed to delete existing framework")
    }


    // Params taken from https://github.com/ge-org/multiplatform-swiftpackage/blob/master/src/main/kotlin/com/chromaticnoise/multiplatformswiftpackage/task/CreateXCFrameworkTask.kt
    executable = "xcodebuild"
    args(mutableListOf<String>().apply {
        add("-create-xcframework")
        add("-output")
        add(xcFrameworkDest.path)
        for (framework in frameworks) {
            add("-framework")
            add(framework.outputFile.path)

            framework.dsymFile().takeIf { it.exists() }?.let { dsymFile ->
                add("-debug-symbols")
                add(dsymFile.path)
            }
        }
    })

    doFirst {
        xcFrameworkDest.deleteRecursively()
    }
}