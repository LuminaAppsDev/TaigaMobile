package com.luminaapps.taigamobile.ui.components.lists

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import com.luminaapps.taigamobile.ui.components.texts.MarkdownText
import com.luminaapps.taigamobile.ui.components.texts.NothingToSeeHereText

@Suppress("FunctionName")
fun LazyListScope.Description(
    description: String,
    mentionableUsers: Map<String, Long> = emptyMap(),
    onMentionClick: ((Long) -> Unit)? = null
) {
    item {
        if (description.isNotEmpty()) {
            MarkdownText(
                text = description,
                modifier = Modifier.fillMaxWidth(),
                members = mentionableUsers,
                onMentionClick = onMentionClick
            )
        } else {
            NothingToSeeHereText()
        }
    }
}
