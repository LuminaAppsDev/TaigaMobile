package com.luminaapps.taigamobile.viewmodels

import com.luminaapps.taigamobile.domain.entities.AuthType
import com.luminaapps.taigamobile.ui.screens.login.LoginViewModel
import com.luminaapps.taigamobile.ui.utils.ErrorResult
import com.luminaapps.taigamobile.ui.utils.SuccessResult
import com.luminaapps.taigamobile.viewmodels.utils.accessDeniedException
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class LoginViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        viewModel = LoginViewModel(mockAppComponent)
    }

    @Test
    fun `test login`(): Unit = runBlocking {
        val password = "password"

        coEvery { mockAuthRepository.auth(any(), any(), neq(password), any()) } throws accessDeniedException

        viewModel.login("", AuthType.Normal, "", password)
        assertIs<SuccessResult<Unit>>(viewModel.loginResult.value)

        viewModel.login("", AuthType.Normal, "", password + "wrong")
        assertIs<ErrorResult<Unit>>(viewModel.loginResult.value)
    }
}
