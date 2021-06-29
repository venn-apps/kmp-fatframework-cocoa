package com.prof18.kmp.fatframework.cocoa.task

import com.prof18.kmp.fatframework.cocoa.data.CocoaPodRepoInfo
import com.prof18.kmp.fatframework.cocoa.data.getConfigurationOrThrow
import com.prof18.kmp.fatframework.cocoa.utils.execBashCommandThrowExecException
import com.prof18.kmp.fatframework.cocoa.utils.retrieveMainBranchName
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import java.io.File

internal const val GENERATE_SPM_TASK_NAME = "generateSPMRepo"

internal fun Project.registerGenerateSPMRepositoryTask() {
    tasks.register(GENERATE_SPM_TASK_NAME) {

        description = "Create a Swift package manager repository to distribute the FatFramework"

        doLast {
            val conf = getConfigurationOrThrow()
            val podspecFileName = "Package.swift"

            val parentFile = File(conf.outputPath)

            val podspecFile = File("${conf.outputPath}/$podspecFileName").apply {
                this.parentFile.mkdirs()
                createNewFile()
            }

            val frameworkName = if (conf.useXCFramework) {
                "${conf.frameworkName}.xcframework"
            } else {
                "${conf.frameworkName}.framework"
            }

            val templateMap = mapOf(
                "name" to conf.frameworkName,
                "version" to conf.versionName,
                "summary" to conf.cocoaPodRepoInfo.summary,
                "homepage" to conf.cocoaPodRepoInfo.homepage,
                "license" to conf.cocoaPodRepoInfo.license,
                "authors" to conf.cocoaPodRepoInfo.authors,
                "gitUrl" to conf.cocoaPodRepoInfo.gitUrl,
                "frameworkName" to frameworkName,
                "frameworkPath" to "${conf.outputPath}/${conf.frameworkName}.${if(conf.useXCFramework) "xcframework" else "framework"}"
            )

            SimpleTemplateEngine()
                .createTemplate(CocoaPodRepoInfo.templateFile)
                .make(templateMap)
                .writeTo(podspecFile.writer())

            execBashCommandThrowExecException(
                output = parentFile,
                commandList = listOf("git", "init"),
                exceptionMessage = "Unable to create the git repository"
            )

            execBashCommandThrowExecException(
                output = parentFile,
                commandList = listOf("git", "add", "."),
                exceptionMessage = "Unable to add changes on main"
            )

            execBashCommandThrowExecException(
                output = parentFile,
                commandList = listOf("git", "commit", "-m", "\"First Commit\""),
                exceptionMessage = "Unable to commit changes on main"
            )

            execBashCommandThrowExecException(
                output = parentFile,
                commandList = listOf("git", "checkout", "-b", "develop"),
                exceptionMessage = "Unable to create the develop branch"
            )

            execBashCommandThrowExecException(
                output = parentFile,
                commandList = listOf("git", "remote", "add", "origin", conf.cocoaPodRepoInfo.gitUrl),
                exceptionMessage = "Unable to push on remote repository"
            )

            val branchName = retrieveMainBranchName(conf.outputPath)
            println(branchName)
            execBashCommandThrowExecException(
                output = parentFile,
                commandList = listOf("git", "push", "origin", "develop", branchName),
                exceptionMessage = "Unable to push on remote repository"
            )
        }
    }
}