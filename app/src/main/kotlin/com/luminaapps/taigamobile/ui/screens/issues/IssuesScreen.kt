package com.luminaapps.taigamobile.ui.screens.issues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import com.luminaapps.taigamobile.domain.entities.CommonTask
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.domain.entities.FiltersData
import com.luminaapps.taigamobile.ui.components.TasksFiltersWithLazyList
import com.luminaapps.taigamobile.ui.components.appbars.ClickableAppBar
import com.luminaapps.taigamobile.ui.components.buttons.PlusButton
import com.luminaapps.taigamobile.ui.components.lists.simpleTasksListWithTitle
import com.luminaapps.taigamobile.ui.screens.main.Routes
import com.luminaapps.taigamobile.ui.theme.TaigaMobileTheme
import com.luminaapps.taigamobile.ui.theme.commonVerticalPadding
import com.luminaapps.taigamobile.ui.theme.mainHorizontalScreenPadding
import com.luminaapps.taigamobile.ui.utils.NavigateToTask
import com.luminaapps.taigamobile.ui.utils.navigateToCreateTaskScreen
import com.luminaapps.taigamobile.ui.utils.navigateToTaskScreen
import com.luminaapps.taigamobile.ui.utils.SubscribeOnError

@Composable
fun IssuesScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {}
) {
    val viewModel: IssuesViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState()

    val issues = viewModel.issues
    issues.SubscribeOnError(showMessage)

    val filters by viewModel.filters.collectAsState()
    filters.SubscribeOnError(showMessage)

    val activeFilters by viewModel.activeFilters.collectAsState()

    IssuesScreenContent(
        projectName = projectName,
        onTitleClick = { navController.navigate(Routes.PROJECTS_SELECTOR) },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Issue) },
        issues = issues,
        filters = filters.data ?: FiltersData(),
        activeFilters = activeFilters,
        selectFilters = viewModel::selectFilters,
        navigateToTask = navController::navigateToTaskScreen
    )
}

@Composable
fun IssuesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    issues: LazyPagingItems<CommonTask>? = null,
    filters: FiltersData = FiltersData(),
    activeFilters: FiltersData = FiltersData(),
    selectFilters: (FiltersData) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ClickableAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    TasksFiltersWithLazyList(
        filters = filters,
        activeFilters = activeFilters,
        selectFilters = selectFilters
    ) {
        simpleTasksListWithTitle(
            commonTasksLazy = issues,
            keysHash = activeFilters.hashCode(),
            navigateToTask = navigateToTask,
            horizontalPadding = mainHorizontalScreenPadding,
            bottomPadding = commonVerticalPadding
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun IssuesScreenPreview() = TaigaMobileTheme {
    IssuesScreenContent(
        projectName = "Cool project"
    )
}
