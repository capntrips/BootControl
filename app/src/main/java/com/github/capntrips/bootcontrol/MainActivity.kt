package com.github.capntrips.bootcontrol

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.capntrips.bootcontrol.ui.theme.BootControlTheme
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService


@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    private var viewModel: MainViewModel? = null
    private lateinit var mainListener: MainListener
    private var bootctlFailure = false;

    inner class BootControlConnection : ServiceConnection {
        @ExperimentalMaterial3Api
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            onBootControlServiceConnected(IBootControlService.Stub.asInterface(service))
        }

        @ExperimentalMaterial3Api
        override fun onServiceDisconnected(name: ComponentName) {
            setContent {
                BootControlTheme {
                    ErrorScreen(stringResource(R.string.root_service_disconnected))
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scale = ObjectAnimator.ofPropertyValuesHolder(
                splashScreenView.view,
                PropertyValuesHolder.ofFloat(
                    View.SCALE_X,
                    1f,
                    0f
                ),
                PropertyValuesHolder.ofFloat(
                    View.SCALE_Y,
                    1f,
                    0f
                )
            )
            scale.interpolator = AccelerateInterpolator()
            scale.duration = 250L
            scale.doOnEnd { splashScreenView.remove() }
            scale.start()
        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel?.isRefreshing?.value == false || bootctlFailure || Shell.isAppGrantedRoot() == false) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        Shell.getShell()
        if (Shell.isAppGrantedRoot()!!) {
            val intent = Intent(this, BootControlService::class.java)
            RootService.bind(intent, BootControlConnection())
        } else {
            setContent {
                BootControlTheme {
                    ErrorScreen(stringResource(R.string.root_required))
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    fun onBootControlServiceConnected(bootctl: IBootControlService) {
        setContent {
            BootControlTheme {
                if (bootctl.halInfo() != null) {
                    val navController = rememberNavController()
                    viewModel = MainViewModel(this, bootctl, navController)
                    if (!viewModel!!.hasError) {
                        mainListener = MainListener {
                            viewModel!!.refresh()
                        }
                        val rebootViewModel = viewModel!!.reboot
                        val isRefreshing by viewModel!!.isRefreshing.collectAsState()
                        BackHandler(enabled = isRefreshing, onBack = {})
                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                RefreshableScreen(viewModel!!, navController, swipeEnabled = true) {
                                    MainContent(viewModel!!, navController)
                                }
                            }
                            composable("reboot") {
                                RefreshableScreen(viewModel!!, navController) {
                                    RebootContent(rebootViewModel)
                                }
                            }
                            composable("error/{error}") { backStackEntry ->
                                val error = backStackEntry.arguments?.getString("error")
                                ErrorScreen(error!!)
                            }
                        }
                    } else {
                        ErrorScreen(viewModel!!.error)
                    }
                } else {
                    bootctlFailure = true
                    ErrorScreen(stringResource(R.string.bootctl_failed))
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (this::mainListener.isInitialized) {
            mainListener.resume()
        }
    }
}
