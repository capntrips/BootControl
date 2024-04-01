package com.github.capntrips.bootcontrol

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun RefreshableScreen(
    viewModel: MainViewModel,
    navController: NavController,
    swipeEnabled: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val state = rememberPullRefreshState(isRefreshing, onRefresh = {
        viewModel.refresh()
    })
    // TODO: Is WindowInsets.statusBars automatically applied to Scaffold?
    // val statusBar = WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues()
    val navigationBars = WindowInsets.navigationBars.asPaddingValues()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // modifier = Modifier.padding(statusBar),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        AnimatedVisibility(
                            !isRefreshing,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = {
            Spacer(
                Modifier
                    .height(navigationBars.calculateBottomPadding())
                    .fillMaxWidth())
        },

    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .pullRefresh(state, swipeEnabled)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                content = content
            )
            PullRefreshIndicator(
                isRefreshing,
                state,
                Modifier.align(Alignment.TopCenter),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.primaryContainer,
                true
            )
        }
    }
}
