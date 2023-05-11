package com.bursa.taksi.ui.view.sign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentSmsVerificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SmsVerificationFragment : Fragment(R.layout.fragment_sms_verification) {
    private var _binding: FragmentSmsVerificationBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SmsVerificationFragmentArgs>()

    private val auth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSmsVerificationBinding.bind(view)
        val vId = args.verificationId

        binding.apply {
            buttonVerify.setOnClickListener {
                val smsCode = textFieldSmsCode.text.toString()
                if (smsCode.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(vId, smsCode)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.warning_enter_sms_code),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.textInputLayoutSmsCode)
                        .show()
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        val action =
                            SmsVerificationFragmentDirections.actionSmsVerificationFragmentToUserNameFragment(
                                user
                            )
                        findNavController().navigate(action)
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_general_2),
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(binding.textInputLayoutSmsCode)
                            .show()
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_sms_code_is_wrong),
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(binding.textInputLayoutSmsCode)
                            .show()
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_general_2),
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(binding.textInputLayoutSmsCode)
                            .show()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}