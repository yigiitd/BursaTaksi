package com.bursa.taksi.ui.view.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountBinding.bind(view)
        binding.apply {
            buttonNavBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}