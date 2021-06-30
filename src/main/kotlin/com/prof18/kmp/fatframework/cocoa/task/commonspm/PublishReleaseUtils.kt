package com.prof18.kmp.fatframework.cocoa.task.commonspm

import com.prof18.kmp.fatframework.cocoa.data.PluginConfig
import com.prof18.kmp.fatframework.cocoa.utils.execBashCommandInRepoAndThrowExecException
import com.prof18.kmp.fatframework.cocoa.utils.retrieveMainBranchName
import org.gradle.api.Project
import org.gradle.process.internal.ExecException
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal fun Project.publishReleaseSPMFramework(config: PluginConfig) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())

    execBashCommandInRepoAndThrowExecException(
        commandList = listOf("git", "add", "."),
        exceptionMessage = "Unable to add the files"
    )

    execBashCommandInRepoAndThrowExecException(
        commandList = listOf(
            "git",
            "commit",
            "-m",
            "\"New release: ${config.versionName} - ${dateFormatter.format(Date())}\""
        ),
        exceptionMessage = "Unable to commit the changes"
    )

    execBashCommandInRepoAndThrowExecException(
        commandList = listOf("git", "tag", config.versionName),
        exceptionMessage = "Unable to tag the commit"
    )

    val branchName = retrieveMainBranchName(config.outputPath)
    execBashCommandInRepoAndThrowExecException(
        commandList = listOf("git", "push", "origin", branchName, "--tags"),
        exceptionMessage = "Unable to push the changes to remote"
    )
}
