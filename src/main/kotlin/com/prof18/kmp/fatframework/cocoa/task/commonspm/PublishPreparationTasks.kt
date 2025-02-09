package com.prof18.kmp.fatframework.cocoa.task.commonspm

import com.prof18.kmp.fatframework.cocoa.data.getConfigurationOrThrow
import com.prof18.kmp.fatframework.cocoa.utils.execBashCommandInRepoAndThrowExecException
import com.prof18.kmp.fatframework.cocoa.utils.executeBashCommand
import com.prof18.kmp.fatframework.cocoa.utils.retrieveMainBranchName
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.process.internal.ExecException

internal const val PREPARE_SPM_REPO_FOR_DEBUG_TASK_NAME = "prepareIosSPMRepoForDebug"
internal const val PREPARE_SPM_REPO_FOR_RELEASE_TASK_NAME = "prepareIosSPMRepoForRelease"

internal fun Project.registerPublishSPMPreparationTasks() {

    tasks.register(PREPARE_SPM_REPO_FOR_DEBUG_TASK_NAME) {
        description = "Prepare Swift Package repository for debug."

        val config = getConfigurationOrThrow()
        // Check if is a git repository
        doLast {
            try {
                executeBashCommand(
                    showOutput = false,
                    workingDirPath = config.outputPath,
                    commandList = listOf("git", "rev-parse", "--is-inside-work-tree")
                )
            } catch (e: ExecException) {
                throw InvalidUserDataException("The provided output folder is not a git repository!")
            }

            // Checkout on develop
            execBashCommandInRepoAndThrowExecException(
                commandList = listOf("git", "checkout", "develop"),
                exceptionMessage = "Error while checking out to the develop branch. Are you sure it does exists?"
            )
        }
    }

    tasks.register(PREPARE_SPM_REPO_FOR_RELEASE_TASK_NAME) {
        description = "Prepare Swift Package for release."

        val config = getConfigurationOrThrow()

        doLast {

            // Check if is a git repository
            try {
                executeBashCommand(
                    showOutput = false,
                    workingDirPath = config.outputPath,
                    commandList = listOf("git", "rev-parse", "--is-inside-work-tree")
                )
            } catch (e: ExecException) {
                throw InvalidUserDataException("The provided output folder is not a git repository!")
            }

            // Check if master or main
            val branchName = retrieveMainBranchName(config.outputPath)

            // Checkout on selected branch
            execBashCommandInRepoAndThrowExecException(
                commandList = listOf("git", "checkout", branchName),
                exceptionMessage = "Error while checking out to the $branchName branch. Are you it does exists?"
            )
        }
    }
}
