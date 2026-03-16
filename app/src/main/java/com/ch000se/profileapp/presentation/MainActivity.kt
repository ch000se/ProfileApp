package com.ch000se.profileapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ch000se.profileapp.presentation.navigation.AppNavGraph
import com.ch000se.profileapp.presentation.navigation.Screen
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            ProfileAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val currentState = state) {
                        MainUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is MainUiState.Ready -> {
                            val startScreen = if (currentState.isUserExist) {
                                Screen.Contacts
                            } else {
                                Screen.CreateProfile
                            }
                            AppNavGraph(
                                windowSize = windowSizeClass.widthSizeClass,
                                startScreen = startScreen
                            )
                        }
                    }
                }
            }
        }
    }
}