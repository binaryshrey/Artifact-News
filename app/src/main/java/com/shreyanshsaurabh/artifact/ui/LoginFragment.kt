package com.shreyanshsaurabh.artifact.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shreyanshsaurabh.artifact.R
import com.shreyanshsaurabh.artifact.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private var playbackPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        requireActivity().window.statusBarColor = Color.parseColor("#000000")
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_login, container, false)

        loopArtifactVid()

        return binding.root
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