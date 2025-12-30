package com.luminaapps.taigamobile.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.*
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.domain.repositories.IUsersRepository
import com.luminaapps.taigamobile.state.*
import com.luminaapps.taigamobile.ui.utils.MutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings
    @Inject lateinit var userRepository: IUsersRepository

    val user = MutableResultFlow<User>()
    val serverUrl by lazy { session.server }

    val themeSetting by lazy { settings.themeSetting }

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        user.loadOrError(preserveValue = false) { userRepository.getMe() }
    }

    fun logout() {
        session.reset()
    }

    fun switchTheme(theme: ThemeSetting) {
        settings.changeThemeSetting(theme)
    }
}
