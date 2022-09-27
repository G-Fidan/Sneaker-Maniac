package com.example.sneaker_maniac.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.sneaker_maniac.api.ApiHelper
import com.example.sneaker_maniac.api.models.ProfileModel
import com.example.sneaker_maniac.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userProfile: MutableLiveData<ProfileModel> = MutableLiveData()
    private val isDone: MutableLiveData<Boolean> = MutableLiveData()
    private val isError: MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        userProfile.observe(viewLifecycleOwner) {
            updateView(it)
        }
        isDone.observe(viewLifecycleOwner) {
            if (it) {
                Snackbar.make(binding.root, "Данные успешно сохранились", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        isError.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
        MainScope().launch(Dispatchers.IO) {
            userProfile.postValue(ApiHelper.getProfile())
        }
        binding.backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnUpdateProfile.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                val response = ApiHelper.updateProfile(
                    ProfileModel(
                        firstName = binding.etNameOwnerCard.text?.split(" ")?.first() ?: "",
                        name = binding.etNameOwnerCard.text?.split(" ")?.last() ?: "",
                        numberCard = binding.etNumberCard.text.toString(),
                        dateCard = binding.etDateCard.text.toString(),
                        cvcCode = binding.etCvcCard.text.toString(),
                        address = binding.etAddress.text.toString(),
                        indexAddress = binding.etIndexAddress.text.toString()
                    )
                )

                if (response.message == "success") {
                    isDone.postValue(true)
                } else {
                    isError.postValue(response.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateView(profile: ProfileModel) {
        if (profile.firstName?.isNotEmpty() == true && profile.name?.isNotEmpty() == true) {
            binding.tvName.text = "${profile.firstName} ${profile.name}}"
            binding.etNameOwnerCard.text =
                Editable.Factory().newEditable("${profile.firstName} ${profile.name}}")
        } else {
            binding.tvName.text = "Your name"
            binding.etNameOwnerCard.text = Editable.Factory().newEditable("")
        }

        binding.etNumberCard.text = Editable.Factory().newEditable(profile.numberCard ?: "")
        binding.etDateCard.text = Editable.Factory().newEditable(profile.dateCard ?: "")
        binding.etCvcCard.text = Editable.Factory().newEditable(profile.cvcCode ?: "")
        binding.etAddress.text = Editable.Factory().newEditable(profile.address ?: "")
        binding.etIndexAddress.text = Editable.Factory().newEditable(profile.indexAddress ?: "")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}