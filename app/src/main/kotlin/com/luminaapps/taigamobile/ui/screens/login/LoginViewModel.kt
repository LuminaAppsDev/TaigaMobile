package com.luminaapps.taigamobile.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.AuthType
import com.luminaapps.taigamobile.domain.repositories.IAuthRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.ui.utils.mutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var authRepository: IAuthRepository
    @Inject lateinit var session: Session

    val loginResult = mutableResultFlow<Unit>()

    val savedServer: String get() = session.server.value

    init {
        appComponent.inject(this)
    }

    fun login(taigaServer: String, authType: AuthType, username: String, password: String) = viewModelScope.launch {
        loginResult.loadOrError(R.string.login_error_message) {
            authRepository.auth(taigaServer, authType, password, username)
        }
    }
}
