package com.luminaapps.taigamobile.ui.screens.main

import androidx.lifecycle.ViewModel
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.state.Settings
import javax.inject.Inject

class MainViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings

    val isLogged by lazy { session.isLogged }
    val isProjectSelected by lazy { session.isProjectSelected }

    val theme by lazy { settings.themeSetting }

    init {
        appComponent.inject(this)
    }
}
