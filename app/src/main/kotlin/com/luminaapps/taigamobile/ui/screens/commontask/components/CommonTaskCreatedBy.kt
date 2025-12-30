package com.luminaapps.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.domain.entities.CommonTaskExtended
import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.ui.components.lists.UserItem

@Suppress("FunctionName")
fun LazyListScope.CommonTaskCreatedBy(
    creator: User,
    commonTask: CommonTaskExtended,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        Text(
            text = stringResource(R.string.created_by),
            style = MaterialTheme.typography.titleMedium
        )

        UserItem(
            user = creator,
            dateTime = commonTask.createdDateTime,
            onUserItemClick = { navigateToProfile(creator.id) }
        )
    }
}
