package com.luminaapps.taigamobile.viewmodels

import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.state.ThemeSetting
import com.luminaapps.taigamobile.ui.screens.settings.SettingsViewModel
import com.luminaapps.taigamobile.ui.utils.ErrorResult
import com.luminaapps.taigamobile.ui.utils.SuccessResult
import com.luminaapps.taigamobile.viewmodels.utils.assertResultEquals
import com.luminaapps.taigamobile.viewmodels.utils.notFoundException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs


class SettingsViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        viewModel = SettingsViewModel(mockAppComponent)
    }

    @BeforeTest
    fun settingOfUsers() {
        coEvery { mockUsersRepository.getMe() } returns mockUser
    }

    companion object {
        val mockUser = mockk<User>(relaxed = true)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        viewModel.onOpen()
        assertResultEquals(SuccessResult(mockUser), viewModel.user.value)

        coEvery { mockUsersRepository.getMe() } throws notFoundException
        viewModel.onOpen()
        assertIs<ErrorResult<User>>(viewModel.user.value)
    }

    @Test
    fun `test logout`(): Unit = runBlocking {
        viewModel.logout()
        coVerify { mockSession.reset() }
    }

    @Test
    fun `test switch theme`(): Unit = runBlocking {
        val themeSetting = ThemeSetting.Light
        viewModel.switchTheme(themeSetting)
        coVerify { mockSettings.changeThemeSetting(eq(themeSetting)) }
    }
}
