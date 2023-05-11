package com.bursa.taksi.ui.view.sign

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentSignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = auth.currentUser
        if (user != null) {
            activity?.let {
                val action = SignUpFragmentDirections.actionSignUpFragmentToMapsFragment(null)
                findNavController().navigate(action)
            }
        }
        auth.useAppLanguage()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_invalid_request),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.textInputLayoutPhoneNumber)
                        .show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_too_many_attempts),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.textInputLayoutPhoneNumber)
                        .show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Unknown Error: ${e.localizedMessage}",
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.textInputLayoutPhoneNumber)
                        .show()
                }
                println(e.localizedMessage)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                val action = SignUpFragmentDirections.actionSignUpFragmentToSmsVerificationFragment(
                    verificationId
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        _binding = FragmentSignupBinding.bind(view)
        binding.apply {
            buttonSignUp.setOnClickListener {
                var phoneNumber = textFieldPhoneNumber.text.toString()
                if (phoneNumber.isNotEmpty()
                ) {
                    phoneNumber = if (phoneNumber.startsWith("90")) {
                        "+$phoneNumber"
                    } else if (phoneNumber.startsWith("0")) {
                        "+9$phoneNumber"
                    } else if (phoneNumber.length == 10) {
                        "+90$phoneNumber"
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.warning_phone_number),
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.warning_blank_spaces),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(binding.textInputLayoutPhoneNumber)
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