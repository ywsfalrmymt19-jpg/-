package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.PreferencesManager
import com.example.tts.ArabicTtsManager
import com.example.ui.BookReaderScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var prefsManager: PreferencesManager
    private lateinit var ttsManager: ArabicTtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefsManager = PreferencesManager(this)
        ttsManager = ArabicTtsManager(this)

        setContent {
            val settings by prefsManager.settings.collectAsState()
            MyApplicationTheme(readerTheme = settings.readerTheme) {
                BookReaderScreen(
                    prefsManager = prefsManager,
                    ttsManager = ttsManager,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.release()
    }
}

