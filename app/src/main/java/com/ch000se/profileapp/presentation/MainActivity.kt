package com.ch000se.profileapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ch000se.profileapp.domain.usecases.IsUserExistUseCase
import com.ch000se.profileapp.presentation.navigation.AppNavGraph
import com.ch000se.profileapp.presentation.navigation.Screen
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var isUserExistUseCase: IsUserExistUseCase

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            var startDestination by remember { mutableStateOf<Screen?>(null) }

            LaunchedEffect(Unit) {
                val userExists = isUserExistUseCase()
                startDestination = if (userExists) Screen.Profile else Screen.CreateProfile
            }

            ProfileAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    startDestination?.let { destination ->
                        AppNavGraph(
                            windowSize = windowSizeClass.widthSizeClass,
                            startDestination = destination
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}