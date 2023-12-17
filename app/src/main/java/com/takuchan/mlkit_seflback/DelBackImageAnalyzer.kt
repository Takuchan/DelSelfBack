package com.takuchan.mlkit_seflback

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

class DelBackImageAnalyzer(
    selfViewModel: SelfViewModel,
    private val listener: (ImageProxy) -> Unit
) : ImageAnalysis.Analyzer {
    val ssselfViewModel = selfViewModel

    private val subjectResultOptions = SubjectSegmenterOptions.SubjectResultOptions.Builder()
        .enableSubjectBitmap()
        .build()

    val options = SubjectSegmenterOptions.Builder()
        .enableMultipleSubjects(subjectResultOptions)
        .build()

    val segmenter = SubjectSegmentation.getClient(options)
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            segmenter.process(image)
                .addOnSuccessListener { segmentationMask ->
                    // Task completed successfully
                    // ...
                    val subjects = segmentationMask.subjects
                    val bitmaps = mutableListOf<Bitmap>()
                    for (subject in subjects) {
                        val mask = subject.bitmap
                        if (mask != null) {
                            bitmaps.add(mask)
                        }
                    }
                    ssselfViewModel.clearImages()
                    ssselfViewModel.setImages(bitmaps)
                    listener(imageProxy)
                    imageProxy.close()

                }
                .addOnCanceledListener() {
                    imageProxy.close()
                }
            // Pass image to an ML Kit Vision API
            // ...
        }

    }
}