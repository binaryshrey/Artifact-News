package com.shreyanshsaurabh.artifact.ui

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.shreyanshsaurabh.artifact.R
import com.shreyanshsaurabh.artifact.databinding.FragmentLoginBinding
import com.shreyanshsaurabh.artifact.utils.NetworkConnection
import com.shreyanshsaurabh.artifact.viewmodel.ArtifactViewModel
import com.shreyanshsaurabh.artifact.viewmodel.ArtifactViewModelFactory


class LoginFragment : Fragment() {

    private var isConnected = false
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var networkConnection: NetworkConnection
    private val avatarsAIViewModel: ArtifactViewModel by activityViewModels {
        ArtifactViewModelFactory(requireNotNull(this.activity).application)
    }

    private lateinit var binding : FragmentLoginBinding
    private var playbackPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        requireActivity().window.statusBarColor = Color.parseColor("#000000")
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity().applicationContext, gso)



        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_login, container, false)

        loopArtifactVid()
        networkConnection = NetworkConnection(requireNotNull(this.activity).application)
        networkConnection.observe(viewLifecycleOwner, Observer { connected ->
            isConnected = connected
        })

        binding.artifactViewModel = avatarsAIViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        avatarsAIViewModel.loginComplete.observe(viewLifecycleOwner, Observer { hasLoggedIn ->
            if (hasLoggedIn) {
                if(isConnected){
                    binding.loginProgress.visibility = View.VISIBLE
                    signInFlow()
                    avatarsAIViewModel.onLoginComplete()
                }
                else{
                    Toast.makeText(context,"NO INTERNET CONNECTION!", Toast.LENGTH_SHORT).show()
                }
            }
        })


        return binding.root
    }

    private fun signInFlow() {
        val signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    if (account != null) {
                        firebaseAuthWithGoogle(account)
                    }
                } else {
                    avatarsAIViewModel.onLoginCancel()
                    Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    Log.e("Login", task.exception.toString())
                }
            }
            if (result.resultCode == Activity.RESULT_CANCELED) {
                avatarsAIViewModel.onLoginCancel()
                binding.loginProgress.visibility = View.GONE
            }
        }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                //verifyIfSavedUserLogsIn()
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPrefsOneFragment())

            } else {
                avatarsAIViewModel.onLoginCancel()
                binding.loginProgress.visibility = View.GONE
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                Log.e("Login", it.exception.toString())

            }
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }
    }


    private fun loopArtifactVid() {
        val uri = Uri.parse("android.resource://com.shreyanshsaurabh.artifact/" + R.raw.artifact_intro)
        binding.videoView.setVideoURI(uri)
        binding.videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            if (playbackPosition != 0) {
                binding.videoView.seekTo(playbackPosition)
            }
            binding.videoView.start()
        }
        binding.videoView.start()
    }


    override fun onStart() {
        super.onStart()
        if (!binding.videoView.isPlaying) {
            binding.videoView.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (binding.videoView.isPlaying) {
            playbackPosition = binding.videoView.currentPosition
            binding.videoView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
    }


}