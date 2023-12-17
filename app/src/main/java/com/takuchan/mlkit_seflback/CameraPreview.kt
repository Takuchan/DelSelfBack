package com.takuchan.mlkit_seflback

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.nfc.Tag
import android.util.Log
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    modifier: Modifier,
    selfViewModel: SelfViewModel,
    cameraExecutorService: ExecutorService
){
    val coroutineScope = rememberCoroutineScope()
    val lifecyclerOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = {context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutorService, DelBackImageAnalyzer(selfViewModel){frameImage ->
                        Log.d("カメラ情報","取得中")
                        val mediaImage = frameImage.image
                        if(mediaImage != null){
                            val image = InputImage.fromMediaImage(mediaImage,frameImage.imageInfo.rotationDegrees)
                        }
                    })
                }
            val previewUseCase: Preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            coroutineScope.launch{
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecyclerOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        previewUseCase,
                        imageAnalyzer
                    )
                }catch (ex: Exception){
                    Log.e(TAG,"Use case binding failed",ex)
                }
            }
            previewView
        }

    )

}