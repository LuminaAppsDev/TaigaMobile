package com.luminaapps.taigamobile.ui.components.texts

import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin

/**
 * Match `@username` only when the `@` is not preceded by a username character.
 * Negative lookbehind is fixed-length (1 char) — variable-length lookbehind is
 * not portable across the regex engines bundled with all minSdk=23 devices.
 * Taiga usernames allow letters, digits, underscores, dashes and internal dots.
 */
private val MENTION_PATTERN = Regex("(?<![A-Za-z0-9_])@([A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+)*)")

/**
 * Use android TextView because Compose does not support Markdown yet
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = true,
    members: Map<String, Long> = emptyMap(),
    onMentionClick: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val markwon = remember(context) {
        Markwon.builder(context)
            .usePlugin(ImagesPlugin.create())
            .build()
    }
    val textSize = MaterialTheme.typography.bodyLarge.fontSize.value
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val mentionColor = MaterialTheme.colorScheme.primary.toArgb()

    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                setTextIsSelectable(isSelectable)
            }
        },
        modifier = modifier,
        update = { tv ->
            tv.textSize = textSize
            tv.setTextColor(textColor)
            val lastRender = tv.getTag(R_ID_LAST_MARKDOWN_RENDER) as? RenderState
            val current = RenderState(text, members, mentionColor)
            if (lastRender != current) {
                markwon.setMarkdown(tv, text)
                applyMentionSpans(tv, members, mentionColor, onMentionClick)
                tv.setTag(R_ID_LAST_MARKDOWN_RENDER, current)
            }
        }
    )
}

private data class RenderState(
    val text: String,
    val members: Map<String, Long>,
    val mentionColor: Int
)

/**
 * Use `View.setTag(int, Any)` with a stable arbitrary key id. `View.setTag(int, ...)`
 * requires an id from an aapt-generated resource namespace; we use the app
 * R.id namespace via a manually-allocated id resource.
 */
private val R_ID_LAST_MARKDOWN_RENDER =
    com.luminaapps.taigamobile.R.id.markdown_text_render_state

/**
 * Post-process the rendered Spannable so resolvable `@username` tokens get a
 * colored, optionally-clickable span. Tokens whose username is not in
 * [members] are left untouched — we don't want to imply a non-existent user.
 */
private fun applyMentionSpans(
    textView: TextView,
    members: Map<String, Long>,
    mentionColor: Int,
    onMentionClick: ((Long) -> Unit)?
) {
    if (members.isEmpty()) return
    val spannable = textView.text as? Spannable ?: return
    var addedClickableSpan = false

    MENTION_PATTERN.findAll(spannable.toString()).forEach { match ->
        val username = match.groupValues[1]
        val userId = members[username] ?: return@forEach
        val start = match.range.first
        val end = match.range.last + 1
        spannable.setSpan(
            ForegroundColorSpan(mentionColor),
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (onMentionClick != null) {
            spannable.setSpan(
                MentionClickableSpan(userId, mentionColor, onMentionClick),
                start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            addedClickableSpan = true
        }
    }

    if (addedClickableSpan) {
        // Must run after setTextIsSelectable, which itself installs an
        // ArrowKeyMovementMethod that swallows clicks on ClickableSpans.
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

private class MentionClickableSpan(
    private val userId: Long,
    private val color: Int,
    private val onClick: (Long) -> Unit
) : ClickableSpan() {
    override fun onClick(widget: View) {
        onClick(userId)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
        ds.isUnderlineText = false
    }
}
