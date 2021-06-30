package com.prof18.kmp.fatframework.cocoa.task.commonspm

import com.prof18.kmp.fatframework.cocoa.data.PluginConfig
import com.prof18.kmp.fatframework.cocoa.utils.execBashCommandInRepoAndThrowExecException
import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.*

internal fun Project.publishDebugSPMFramework(config: PluginConfig) {
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
            "\"New debug release: ${config.versionName} - ${dateFormatter.format(Date())}\""
        ),
        exceptionMessage = "Unable to commit the changes"
    )

    execBashCommandInRepoAndThrowExecException(
        commandList = listOf("git", "push", "origin", "develop"),
        exceptionMessage = "Unable to push the changes to remote"
    )
}
