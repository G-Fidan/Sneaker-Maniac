package com.example.sneaker_maniac.fragments.login

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.sneaker_maniac.App
import com.example.sneaker_maniac.Properties
import com.example.sneaker_maniac.R
import com.example.sneaker_maniac.api.ApiHelper
import com.example.sneaker_maniac.api.ApiManager
import com.example.sneaker_maniac.api.IInternetConnected
import com.example.sneaker_maniac.api.models.LoginRequest
import com.example.sneaker_maniac.databinding.FragmentLoginBinding
import com.example.sneaker_maniac.fragments.main.MainFragment
import com.example.sneaker_maniac.fragments.registration.RegistrationFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val isLogin: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isError: MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStart() {
        super.onStart()
        ApiManager.setConnectCallback(requireContext(), object : IInternetConnected {
            override fun onConnect() {

            }

            override fun onLost() {
                Snackbar.make(
                    binding.root,
                    "Отсутствует интернет соединение",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        })
        isLogin.observe(viewLifecycleOwner) {
            if (it) {
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
        isError.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
        binding.btnLogin.setOnClickListener {
            if (Properties.isNetworkConnect) {
                if (binding.etEmail.text?.isNotEmpty() == true && binding.etPassword.text?.isNotEmpty() == true) {
                    MainScope().launch(Dispatchers.IO) {
                        val response = ApiHelper.login(
                            LoginRequest(
                                email = binding.etEmail.text.toString(),
                                password = binding.etPassword.text.toString()
                            )
                        )
                        if (response.token?.isNotEmpty() == true) {
                            val ed: Editor = requireActivity().getSharedPreferences(
                                App.appContext.getString(R.string.app_name),
                                MODE_PRIVATE
                            ).edit()
                            ed.putString("token", response.token)
                            ed.apply()
                            isLogin.postValue(true)
                        } else {
                            Log.d("response", response.message.toString())
                            when (response.message) {
                                "uncorrected" -> {
                                    isError.postValue("Неправильный логин или пароль")
                                }
                                "no params" -> {
                                    isError.postValue("Отсутствуют некоторые параметры")
                                }
                                else -> {
                                    isError.postValue("Непредвиденная ошибка")
                                }
                            }
                        }
                    }
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Отсутствует интернет соединение",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnGoToRegistration.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .add(
                    Properties.containerId,
                    RegistrationFragment(),
                    RegistrationFragment::javaClass.name
                )
                .addToBackStack(RegistrationFragment::javaClass.name)
                .commit()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}