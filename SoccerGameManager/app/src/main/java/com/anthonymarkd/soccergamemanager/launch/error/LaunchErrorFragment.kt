package com.anthonymarkd.soccergamemanager.launch.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anthonymarkd.soccergamemanager.R
import com.anthonymarkd.soccergamemanager.main.MainViewModel
import kotlinx.android.synthetic.main.launch_error_fragment.*
import kotlinx.android.synthetic.main.launch_error_fragment.view.*

class LaunchErrorFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance() = LaunchErrorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Check for animation's last left off frame before leaving
        val frameLeftOff = savedInstanceState?.getInt("frame", 0)
        val view = inflater.inflate(R.layout.launch_error_fragment, container, false)
        // set the frame, 0 if null else int
        view.lottieAnimationViewLaunchError.frame = frameLeftOff ?: 0
        view.lottieAnimationViewLaunchError.playAnimation()

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }


        view.retryButton.setOnClickListener {
            viewModel.launchDataLoad()
        }
        return view

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save state of animation last left off frame

        val frameLeftOff = lottieAnimationViewLaunchError.frame
        outState.putInt("frame", frameLeftOff)
    }
}