package com.luminaapps.taigamobile.dagger

import android.content.Context
import com.luminaapps.taigamobile.ui.screens.commontask.CommonTaskViewModel
import com.luminaapps.taigamobile.ui.screens.createtask.CreateTaskViewModel
import com.luminaapps.taigamobile.ui.screens.dashboard.DashboardViewModel
import com.luminaapps.taigamobile.ui.screens.epics.EpicsViewModel
import com.luminaapps.taigamobile.ui.screens.issues.IssuesViewModel
import com.luminaapps.taigamobile.ui.screens.kanban.KanbanViewModel
import com.luminaapps.taigamobile.ui.screens.login.LoginViewModel
import com.luminaapps.taigamobile.ui.screens.main.MainViewModel
import com.luminaapps.taigamobile.ui.screens.profile.ProfileViewModel
import com.luminaapps.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import com.luminaapps.taigamobile.ui.screens.scrum.ScrumViewModel
import com.luminaapps.taigamobile.ui.screens.settings.SettingsViewModel
import com.luminaapps.taigamobile.ui.screens.sprint.SprintViewModel
import com.luminaapps.taigamobile.ui.screens.team.TeamViewModel
import com.luminaapps.taigamobile.ui.screens.wiki.createpage.WikiCreatePageViewModel
import com.luminaapps.taigamobile.ui.screens.wiki.list.WikiListViewModel
import com.luminaapps.taigamobile.ui.screens.wiki.page.WikiPageViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, RepositoriesModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder
        fun build(): AppComponent
    }

    fun inject(mainViewModel: MainViewModel)
    fun inject(loginViewModel: LoginViewModel)
    fun inject(dashboardViewModel: DashboardViewModel)
    fun inject(scrumViewModel: ScrumViewModel)
    fun inject(epicsViewModel: EpicsViewModel)
    fun inject(projectSelectorViewModel: ProjectSelectorViewModel)
    fun inject(sprintViewModel: SprintViewModel)
    fun inject(commonTaskViewModel: CommonTaskViewModel)
    fun inject(teamViewModel: TeamViewModel)
    fun inject(settingsViewModel: SettingsViewModel)
    fun inject(createTaskViewModel: CreateTaskViewModel)
    fun inject(issuesViewModel: IssuesViewModel)
    fun inject(kanbanViewModel: KanbanViewModel)
    fun inject(profileViewModel: ProfileViewModel)
    fun inject(wikiSelectorViewModel: WikiListViewModel)
    fun inject(wikiPageViewModel: WikiPageViewModel)
    fun inject(wikiCreatePageViewModel: WikiCreatePageViewModel)
}