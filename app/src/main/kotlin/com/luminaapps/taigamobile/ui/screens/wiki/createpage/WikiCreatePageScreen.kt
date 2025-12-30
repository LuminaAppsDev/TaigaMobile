package com.luminaapps.taigamobile.ui.screens.wiki.createpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.ui.components.dialogs.LoadingDialog
import com.luminaapps.taigamobile.ui.components.editors.Editor
import com.luminaapps.taigamobile.ui.utils.LoadingResult
import com.luminaapps.taigamobile.ui.utils.SuccessResult
import com.luminaapps.taigamobile.ui.utils.navigateToWikiPageScreen
import com.luminaapps.taigamobile.ui.utils.subscribeOnError

@Composable
fun WikiCreatePageScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: WikiCreatePageViewModel = viewModel()

    val creationResult by viewModel.creationResult.collectAsState()
    creationResult.subscribeOnError(showMessage)

    creationResult.takeIf { it is SuccessResult }?.data?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
            navController.navigateToWikiPageScreen(it.slug)
        }
    }

    WikiCreatePageScreenContent(
        isLoading = creationResult is LoadingResult,
        createWikiPage = viewModel::createWikiPage,
        navigateBack = navController::popBackStack
    )
}

@Composable
fun WikiCreatePageScreenContent(
    isLoading: Boolean = false,
    createWikiPage: (title: String, description: String) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {}
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    Editor(
        toolbarText = stringResource(R.string.create_new_page),
        onSaveClick = createWikiPage,
        navigateBack = navigateBack
    )

    if (isLoading) {
        LoadingDialog()
    }
}

@Preview
@Composable
fun WikiCreatePageScreenPreview() {
    WikiCreatePageScreenContent()
}
