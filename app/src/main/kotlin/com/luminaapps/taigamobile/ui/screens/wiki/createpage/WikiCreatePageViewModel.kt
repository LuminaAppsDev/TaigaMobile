package com.luminaapps.taigamobile.ui.screens.wiki.createpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.WikiPage
import com.luminaapps.taigamobile.domain.repositories.IWikiRepository
import com.luminaapps.taigamobile.ui.utils.mutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class WikiCreatePageViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    @Inject
    lateinit var wikiRepository: IWikiRepository

    val creationResult = mutableResultFlow<WikiPage>()

    init {
        appComponent.inject(this)
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