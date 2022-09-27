package com.example.sneaker_maniac.fragments.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.sneaker_maniac.api.ApiHelper
import com.example.sneaker_maniac.api.models.LoginRequest
import com.example.sneaker_maniac.databinding.FragmentRegistrationBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val isLogin: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isError: MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        isLogin.observe(viewLifecycleOwner) {
            if (it) {
                Snackbar.make(binding.root, "Аккаунт успешно создан", Snackbar.LENGTH_SHORT).show()
                requireActivity()
                    .supportFragmentManager
                    .popBackStack()
            }
        }
        isError.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
        binding.btnRegister.setOnClickListener {
            if (binding.etEmail.text?.isNotEmpty() == true && binding.etPassword.text?.isNotEmpty() == true && binding.etPhone.text?.isNotEmpty() == true) {
                MainScope().launch(Dispatchers.IO) {
                    val response = ApiHelper.registration(
                        LoginRequest(
                            email = binding.etEmail.text.toString(),
                            password = binding.etPassword.text.toString(),
                            phone = binding.etPhone.text.toString()
                        )
                    )
                    Log.d("res", response.message.toString())
                    when (response.message) {
                        "any db error" -> {
                            isError.postValue("Ошибка сервера, попробуйте позже")
                        }
                        "no params" -> {
                            isError.postValue("Отсутствуют некоторые параметры")
                        }
                        "success" -> {
                            isLogin.postValue(true)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}