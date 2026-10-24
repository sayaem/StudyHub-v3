package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.StudyHubViewModel
import com.example.ui.navigation.StudyHubApp

class MainActivity : ComponentActivity() {
    private val viewModel: StudyHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyHubApp(viewModel = viewModel)
        }
    }
}
