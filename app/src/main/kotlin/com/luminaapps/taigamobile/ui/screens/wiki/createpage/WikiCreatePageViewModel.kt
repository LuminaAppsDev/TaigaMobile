package com.luminaapps.taigamobile.ui.screens.wiki.createpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.domain.entities.WikiPage
import com.luminaapps.taigamobile.domain.repositories.IUsersRepository
import com.luminaapps.taigamobile.domain.repositories.IWikiRepository
import com.luminaapps.taigamobile.ui.utils.mutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class WikiCreatePageViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    @Inject
    lateinit var wikiRepository: IWikiRepository

    @Inject
    lateinit var usersRepository: IUsersRepository

    val creationResult = mutableResultFlow<WikiPage>()
    val team = mutableResultFlow<List<User>>()

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        team.loadOrError(showLoading = false) {
            usersRepository.getTeam().map { it.toUser() }
        }
    }

    fun createWikiPage(title: String, content: String) = viewModelScope.launch {
        creationResult.loadOrError {
            val slug = title.replace(" ", "-").lowercase()

            wikiRepository.createWikiLink(
                href = slug,
                title = title
            )

            // Need it, because we can't put content to page
            // and create link for it at the same time :(
            val wikiPage = wikiRepository.getProjectWikiPageBySlug(slug)

            wikiRepository.editWikiPage(
                pageId = wikiPage.id,
                content = content,
                version = wikiPage.version
            )

            wikiPage
        }
    }
}