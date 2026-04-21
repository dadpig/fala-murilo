package com.tairone.falamurilo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tairone.falamurilo.ui.ContentScreen
import com.tairone.falamurilo.ui.theme.FalaMuriloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FalaMuriloTheme {
                ContentScreen()
            }
        }
    }
}
