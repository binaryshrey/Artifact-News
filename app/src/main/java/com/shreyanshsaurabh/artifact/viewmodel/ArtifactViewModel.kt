package com.shreyanshsaurabh.artifact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class ArtifactViewModel(application: Application) : AndroidViewModel(application)  {


    //login
    private val _loginComplete = MutableLiveData<Boolean>()
    val loginComplete: LiveData<Boolean>
        get() = _loginComplete

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    private val _navigateToHomeFromPrefs = MutableLiveData<Boolean>()
    val navigateToHomeFromPrefs: LiveData<Boolean>
        get() = _navigateToHomeFromPrefs

    private val _navigateToHomeFromNew = MutableLiveData<Boolean>()
    val navigateToHomeFromNew: LiveData<Boolean>
        get() = _navigateToHomeFromNew


    init {
        _loginComplete.value = false
        _navigateToHome.value = false
        _navigateToHomeFromPrefs.value = false
        _navigateToHomeFromNew.value = false
    }



    fun navigateToHomeComplete(){
        _navigateToHome.value = false
    }

    fun updateNavigateHomeFromPrefsComplete(){
        _navigateToHomeFromPrefs.value = false
    }

    fun updateNavigateHomeFromNewComplete(){
        _navigateToHomeFromNew.value = false
    }


    //login-checked
    fun updateLogin() {
        _loginComplete.value = true
    }
    fun onLoginComplete() {
        _loginComplete.value = false
    }
    fun onLoginCancel() {
        _loginComplete.value = false
    }


}