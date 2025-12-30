package com.luminaapps.taigamobile.ui.screens.wiki.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.WikiLink
import com.luminaapps.taigamobile.domain.entities.WikiPage
import com.luminaapps.taigamobile.domain.repositories.IWikiRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.ui.utils.MutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class WikiListViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var wikiRepository: IWikiRepository

    val projectName by lazy { session.currentProjectName }

    val wikiPages = MutableResultFlow<List<WikiPage>>()
    val wikiLinks = MutableResultFlow<List<WikiLink>>()

    init {
        appComponent.inject(this)
    }

    fun onOpen() {
        getWikiPage()
        getWikiLinks()
    }

    fun getWikiPage() = viewModelScope.launch {
        wikiPages.loadOrError {
            wikiRepository.getProjectWikiPages()
        }
    }

    fun getWikiLinks() = viewModelScope.launch {
        wikiLinks.loadOrError {
            wikiRepository.getWikiLinks()
        }
    }
}