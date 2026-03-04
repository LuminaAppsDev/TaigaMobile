package com.luminaapps.taigamobile.ui.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.luminaapps.taigamobile.ui.theme.mainHorizontalScreenPadding
import kotlinx.coroutines.launch

/**
 * Swipeable tabs
 */
@Composable
fun HorizontalTabbedPager(
    tabs: Array<out Tab>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(pageCount = { tabs.size }),
    scrollable: Boolean = true,
    edgePadding: Dp = mainHorizontalScreenPadding,
    content: @Composable (page: Int) -> Unit
) = Column(modifier = modifier) {
    val coroutineScope = rememberCoroutineScope()

    val indicator: @Composable (tabPositions: List<androidx.compose.material3.TabPosition>) -> Unit = { tabPositions ->
        if (tabPositions.isNotEmpty()) {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
            )
        }
    }

    val tabsRow: @Composable () -> Unit = {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                text = {
                    Text(
                        text = stringResource(tab.titleId),
                        color = if (pagerState.currentPage == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            )
        }
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = edgePadding,
            indicator = indicator,
            divider = {},
            tabs = tabsRow
        )
    } else {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = indicator,
            divider = {},
            tabs = tabsRow
        )
    }

    Spacer(Modifier.height(8.dp))

    HorizontalPager(state = pagerState) { page ->
        content(page)
    }
}

interface Tab {
    val titleId: Int
}
