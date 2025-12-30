package com.luminaapps.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Comment(
    val id: String,
    @param:Json(name = "user") val author: User,
    @param:Json(name = "comment") val text: String,
    @param:Json(name = "created_at") val postDateTime: LocalDateTime,
    @param:Json(name = "delete_comment_date") val deleteDate: LocalDateTime?
) {
    var canDelete: Boolean = false
}
