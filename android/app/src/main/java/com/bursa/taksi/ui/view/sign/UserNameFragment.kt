package com.bursa.taksi.ui.view.sign

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentUserNameBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserNameFragment : Fragment(R.layout.fragment_user_name) {
    private var _binding: FragmentUserNameBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UserNameFragmentArgs>()

    private val auth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserNameBinding.bind(view)
        val currentUser = auth.currentUser
        if (args.user != currentUser) {
            Toast.makeText(
                requireContext(),
                getString(R.string.error_signup),
                Toast.LENGTH_LONG
            ).show()
            val action = UserNameFragmentDirections.actionUserNameFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.apply {
            buttonComplete.setOnClickListener {
                val fullName = textFieldFullName.text.toString()

                if (fullName.isNotEmpty()) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullName
                    }
                    currentUser!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.text_login_successfull),
                                    Toast.LENGTH_LONG
                                ).show()
                                val action =
                                    UserNameFragmentDirections.actionUserNameFragmentToMapsFragment(null)
                                findNavController().navigate(action)

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.error_general),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.warning_enter_name),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.textInputLayoutFullName).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}