package com.luminaapps.taigamobile.ui.components.editors

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.domain.entities.User

private const val MAX_PICKER_SUGGESTIONS = 6

/**
 * TextFieldWithHint augmented with an @-mention picker. When the user types `@`
 * at the start of a word, a popup appears above the field listing project
 * members whose username or display name matches the partial token; selecting
 * one replaces `@<partial>` with `@<username> ` at the cursor.
 *
 * Pass an empty [members] list to disable mention behaviour (the field then
 * behaves identically to [TextFieldWithHint]).
 */
@Composable
fun MentionableTextField(
    @StringRes hintId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    members: List<User>,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp,
    width: Dp? = null,
    minHeight: Dp? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusChange: (Boolean) -> Unit = {},
    focusRequester: FocusRequester = remember { FocusRequester() },
    maxLines: Int = Int.MAX_VALUE,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onSearchClick: (() -> Unit)? = null,
    hasBorder: Boolean = false,
    contentAlignment: Alignment = Alignment.CenterStart
) {
    val mention = remember(value, members) { detectActiveMention(value) }
    val suggestions = remember(mention, members) {
        if (mention == null || members.isEmpty()) emptyList()
        else members.filter { matchesUser(it, mention.token) }.take(MAX_PICKER_SUGGESTIONS)
    }

    Box {
        TextFieldWithHint(
            hintId = hintId,
            value = value,
            onValueChange = onValueChange,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            width = width,
            minHeight = minHeight,
            style = style,
            singleLine = singleLine,
            keyboardType = keyboardType,
            onFocusChange = onFocusChange,
            focusRequester = focusRequester,
            maxLines = maxLines,
            textColor = textColor,
            onSearchClick = onSearchClick,
            hasBorder = hasBorder,
            contentAlignment = contentAlignment
        )

        if (mention != null && suggestions.isNotEmpty()) {
            val gapPx = with(LocalDensity.current) { 4.dp.roundToPx() }
            Popup(
                popupPositionProvider = AboveAnchorPositionProvider(gapPx),
                onDismissRequest = { /* dismissed by typing past the mention */ },
                properties = PopupProperties(focusable = false)
            ) {
                MentionPicker(
                    suggestions = suggestions,
                    onSelect = { user ->
                        onValueChange(insertMention(value, mention, user.username))
                    }
                )
            }
        }
    }
}

@Composable
private fun MentionPicker(
    suggestions: List<User>,
    onSelect: (User) -> Unit
) = Surface(
    shape = MaterialTheme.shapes.medium,
    tonalElevation = 6.dp,
    shadowElevation = 6.dp,
    modifier = Modifier.widthIn(min = 220.dp, max = 320.dp)
) {
    Column {
        suggestions.forEach { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(user) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.avatarUrl ?: R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Place the popup directly above the anchor field with a small gap. If there
 * is not enough room above (anchor near the screen top), fall back to placing
 * it below.
 */
private class AboveAnchorPositionProvider(private val gapPx: Int) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val x = anchorBounds.left.coerceAtMost(windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val above = anchorBounds.top - popupContentSize.height - gapPx
        val y = if (above >= 0) above else anchorBounds.bottom + gapPx
        return IntOffset(x, y)
    }
}

internal data class MentionContext(val startIndex: Int, val token: String)

/**
 * Walks back from the cursor to find a `@<token>` that is currently being
 * typed. Returns null if the cursor is not inside an active mention (e.g.
 * the prior `@` is in the middle of a word, or whitespace lies between the
 * `@` and the cursor).
 */
internal fun detectActiveMention(value: TextFieldValue): MentionContext? {
    val cursor = value.selection.start
    if (value.selection.end != cursor) return null
    val text = value.text
    if (cursor == 0 || cursor > text.length) return null

    var i = cursor - 1
    while (i >= 0) {
        val ch = text[i]
        if (ch == '@') {
            val precededByBoundary = i == 0 || text[i - 1].isWhitespace()
            if (!precededByBoundary) return null
            return MentionContext(startIndex = i, token = text.substring(i + 1, cursor))
        }
        if (ch.isWhitespace()) return null
        i--
    }
    return null
}

internal fun insertMention(value: TextFieldValue, mention: MentionContext, username: String): TextFieldValue {
    val text = value.text
    val before = text.substring(0, mention.startIndex)
    val after = text.substring(value.selection.start)
    val replacement = "@$username "
    val newText = before + replacement + after
    val cursorPos = before.length + replacement.length
    return TextFieldValue(
        text = newText,
        selection = androidx.compose.ui.text.TextRange(cursorPos)
    )
}

private fun matchesUser(user: User, token: String): Boolean {
    if (token.isEmpty()) return true
    return user.username.contains(token, ignoreCase = true) ||
        user.displayName.contains(token, ignoreCase = true)
}
