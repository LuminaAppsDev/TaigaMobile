package com.luminaapps.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Project related entities
 */

@JsonClass(generateAdapter = true)
data class Project(
    val id: Long,
    val name: String,
    val slug: String,
    @param:Json(name = "i_am_member") val isMember: Boolean = false,
    @param:Json(name = "i_am_admin") val isAdmin: Boolean = false,
    @param:Json(name = "i_am_owner") val isOwner: Boolean = false,
    val description: String? = null,
    @param:Json(name = "logo_small_url") val avatarUrl: String? = null,
    val members: List<Long> = emptyList(),
    @param:Json(name = "total_fans") val fansCount: Int = 0,
    @param:Json(name = "total_watchers") val watchersCount: Int = 0,
    @param:Json(name = "is_private") val isPrivate: Boolean = false
)