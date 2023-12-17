package com.takuchan.mlkit_seflback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.takuchan.mlkit_seflback.ui.theme.MLKit_seflbackTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private lateinit var selfViewModel: SelfViewModel
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selfViewModel = ViewModelProvider(this)[SelfViewModel::class.java]
        cameraExecutor = Executors.newSingleThreadExecutor()
        setContent {
            MLKit_seflbackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val images = selfViewModel.image.observeAsState(initial = mutableListOf())
                    Column(modifier = Modifier.fillMaxSize()) {
                        CameraPreview(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),selfViewModel = selfViewModel,
                            cameraExecutorService = cameraExecutor)
                        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            images.value.forEach {
                                Text(text = it.toString())
                            }
                            LazyColumn{
                                items(images.value){ bitmap->

                                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,modifier = Modifier.rotate(90f).size(300.dp))
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

