package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mankomaniaclient.websocket.ServerCommunicationClient

/**
 * # MainActivity
 *
 * Haupt-Einstiegspunkt der Android-App.
 *
 * @author
 * @since
 * @description Diese Activity wird beim App-Start geladen und initialisiert die UI.
 */
class MainActivity : ComponentActivity() {

    /**
     * Standard-Android-Lifecyclemethode.
     * Hier könnte zukünftig die UI gesetzt werden.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO: Hier GlobalTheme() und einen Screen aufrufen
        }
        ServerCommunicationClient.start()

    }
}