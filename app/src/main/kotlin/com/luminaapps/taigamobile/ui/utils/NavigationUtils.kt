package com.luminaapps.taigamobile.ui.utils

import androidx.navigation.NavController
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.ui.screens.main.Routes

/**
 * Since navigating to some screens requires several arguments, here are some utils
 * to make navigation code more readable
 */

typealias NavigateToTask = (id: Long, type: CommonTaskType, ref: Int) -> Unit
fun NavController.navigateToTaskScreen(id: Long, type: CommonTaskType, ref: Int)
    = navigate("${Routes.COMMON_TASK}/$id/$type/$ref")

typealias NavigateToCreateTask = (type: CommonTaskType, parentId: Long?, sprintId: Long?, statusId: Long, swimlaneId: Long?) -> Unit
fun NavController.navigateToCreateTaskScreen(
    type: CommonTaskType,
    parentId: Long? = null,
    sprintId: Long? = null,
    statusId: Long? = null,
    swimlaneId: Long? = null
) = Routes.Arguments.let { navigate("${Routes.CREATE_TASK}/$type?${it.PARENT_ID}=${parentId ?: -1}&${it.SPRINT_ID}=${sprintId ?: -1}&${it.STATUS_ID}=${statusId ?: -1}&${it.SWIMLANE_ID}=${swimlaneId ?: -1}") }

typealias NavigateToSprint = (sprintId: Long) -> Unit
fun NavController.navigateToSprint(sprintId: Long) = navigate("${Routes.SPRINT}/$sprintId")


typealias NavigateToProfile = (id: Long) -> Unit
fun NavController.navigateToProfileScreen(id: Long)
    = navigate("${Routes.PROFILE}/$id")

typealias NavigateToWikiPage = (slug: String) -> Unit
fun NavController.navigateToWikiPageScreen(slug: String)
    = navigate("${Routes.WIKI_PAGE}/$slug")