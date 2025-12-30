package com.luminaapps.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class WikiPage(
    val id: Long,
    val version: Int,
    val content: String,
    val editions: Long,
    @param:Json(name = "created_date") val cratedDate: LocalDateTime,
    @param:Json(name = "is_watcher") val isWatcher: Boolean,
    @param:Json(name = "last_modifier") val lastModifier: Long,
    @param:Json(name = "modified_date") val modifiedDate: LocalDateTime,
    @param:Json(name = "total_watchers") val totalWatchers: Long,
    @param:Json(name = "slug")val slug: String
)

@JsonClass(generateAdapter = true)
data class WikiLink(
    @param:Json(name = "href") val ref: String,
    val id: Long,
    val order: Long,
    val title: String
)