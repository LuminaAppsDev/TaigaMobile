package com.luminaapps.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Users related entities
 */

@JsonClass(generateAdapter = true)
data class User(
    @param:Json(name = "id") val _id: Long?,
    @param:Json(name = "full_name_display") val fullName: String?,
    val photo: String?,
    @param:Json(name = "big_photo") val bigPhoto: String?,
    val username: String,
    val name: String? = null, // sometimes name appears here
    val pk: Long? = null
) {
    val displayName get() = fullName ?: name!!
    val avatarUrl get() = bigPhoto ?: photo
    val id get() = _id ?: pk!!
}



data class TeamMember(
    val id: Long,
    val avatarUrl: String?,
    val name: String,
    val role: String,
    val username: String,
    val totalPower: Int
) {
    fun toUser() = User(
        _id = id,
        fullName = name,
        photo = avatarUrl,
        bigPhoto = null,
        username = username
    )
}

@JsonClass(generateAdapter = true)
data class Stats(
    val roles: List<String> = emptyList(),
    @param:Json(name = "total_num_closed_userstories")
    val totalNumClosedUserStories: Int,
    @param:Json(name = "total_num_contacts")
    val totalNumContacts: Int,
    @param:Json(name = "total_num_projects")
    val totalNumProjects: Int,
)