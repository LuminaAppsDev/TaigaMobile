package com.luminaapps.taigamobile.ui.screens.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.domain.entities.TeamMember
import com.luminaapps.taigamobile.ui.components.appbars.ClickableAppBar
import com.luminaapps.taigamobile.ui.components.loaders.CircularLoader
import com.luminaapps.taigamobile.ui.components.texts.NothingToSeeHereText
import com.luminaapps.taigamobile.ui.screens.main.Routes
import com.luminaapps.taigamobile.ui.theme.TaigaMobileTheme
import com.luminaapps.taigamobile.ui.theme.mainHorizontalScreenPadding
import com.luminaapps.taigamobile.ui.utils.LoadingResult
import com.luminaapps.taigamobile.ui.utils.navigateToProfileScreen
import com.luminaapps.taigamobile.ui.utils.SubscribeOnError

@Composable
fun TeamScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: TeamViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState()

    val team by viewModel.team.collectAsState()
    team.SubscribeOnError(showMessage)

    TeamScreenContent(
        projectName = projectName,
        team = team.data.orEmpty(),
        isLoading = team is LoadingResult,
        onTitleClick = { navController.navigate(Routes.PROJECTS_SELECTOR) },
        navigateBack = navController::popBackStack,
        onUserItemClick = { userId ->
            navController.navigateToProfileScreen(userId)
        }
    )
}

@Composable
fun TeamScreenContent(
    projectName: String,
    team: List<TeamMember> = emptyList(),
    isLoading: Boolean = false,
    onTitleClick: () -> Unit = {},
    navigateBack: () -> Unit = {},
    onUserItemClick: (userId: Long) -> Unit = { _ -> }
) = Column(Modifier.fillMaxSize()) {
    ClickableAppBar(
        projectName = projectName,
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        team.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NothingToSeeHereText()
            }
        }
        else -> {
            LazyColumn(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                items(team) { member ->
                    TeamMemberItem(
                        teamMember = member,
                        onUserItemClick = { onUserItemClick(member.id) }
                    )
                    Spacer(Modifier.height(6.dp))
                }

                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars.add(WindowInsets(bottom = 8.dp))))
                }
            }
        }
    }
}

@Composable
private fun TeamMemberItem(
    teamMember: TeamMember,
    onUserItemClick: () -> Unit
) = Row(
    modifier = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = LocalIndication.current,
        onClick = { onUserItemClick() }
    ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.6f)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(teamMember.avatarUrl ?: R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text = teamMember.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = teamMember.role,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.weight(0.4f)
    ) {
        Text(
            text = teamMember.totalPower.toString(),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.power),
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TeamScreenPreview() = TaigaMobileTheme {
    TeamScreenContent(
        projectName = "Name",
        team = List(3) {
            TeamMember(
                id = 0L,
                avatarUrl = null,
                name = "First Last",
                role = "Cool guy",
                username = "username",
                totalPower = 14
            )
        }
    )
}