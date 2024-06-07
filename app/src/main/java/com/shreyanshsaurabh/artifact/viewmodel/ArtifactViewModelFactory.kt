package com.shreyanshsaurabh.artifact.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ArtifactViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtifactViewModel::class.java)) {
            return ArtifactViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}