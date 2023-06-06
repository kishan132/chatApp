package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.databinding.FragmentSampleBinding

class SampleFragment : Fragment() {

    private var binding: FragmentSampleBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSampleBinding.inflate(inflater, container, false)

        binding?.tvTitle?.text = "this is Sample Fragment"

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}