package com.takuchan.mlkit_seflback

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelfViewModel: ViewModel() {
    private val _image = MutableLiveData<MutableList<Bitmap>>()
    val image: LiveData<MutableList<Bitmap>> = _image

    fun clearImages(){
        _image.value = mutableListOf()
    }
    fun setImages(images: MutableList<Bitmap>){
        _image.value = images
    }
}