package com.example.sneaker_maniac.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sneaker_maniac.Properties
import com.example.sneaker_maniac.databinding.FragmentLoginBinding
import com.example.sneaker_maniac.fragments.main.MainFragment
import com.example.sneaker_maniac.fragments.registration.RegistrationFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text?.isNotEmpty() == true && binding.etPassword.text?.isNotEmpty() == true) {
                requireActivity()
                    .supportFragmentManager
                    .beginTransaction()
                    .replace(
                        Properties.containerId,
                        MainFragment(),
                        MainFragment::javaClass.name
                    )
                    .commit()
            }
        }

        binding.btnGoToRegistration.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(
                    Properties.containerId,
                    RegistrationFragment(),
                    RegistrationFragment::javaClass.name
                )
                .commit()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}