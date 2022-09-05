package ru.cherepanov.apps.dictionary

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import ru.cherepanov.apps.dictionary.ui.navigation.AppNavHost
import ru.cherepanov.apps.dictionary.ui.navigation.Args

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var args by mutableStateOf<Args?>(null)

        args = intent.getArgs(isActivityArgs = true)
        addOnNewIntentListener {
            args = it.getArgs()
        }
        setContent {
            AppNavHost(
                args = args,
                onArgsProcessed = { args = null }
            )
        }
    }

    private fun Intent.getArgs(isActivityArgs: Boolean = false): Args? {
        val searchTerm = getStringExtra(Intent.EXTRA_PROCESS_TEXT)
        return searchTerm?.let { Args(searchTerm, isActivityArgs) }
    }
}