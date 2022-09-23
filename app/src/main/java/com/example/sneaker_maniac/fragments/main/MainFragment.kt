package com.example.sneaker_maniac.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.sneaker_maniac.Properties
import com.example.sneaker_maniac.api.ApiHelper
import com.example.sneaker_maniac.api.ApiManager
import com.example.sneaker_maniac.api.IInternetConnected
import com.example.sneaker_maniac.api.models.ProductResponse
import com.example.sneaker_maniac.databinding.FragmentMainBinding
import com.example.sneaker_maniac.fragments.cart.CartFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var productsList: MutableLiveData<List<ProductResponse>> = MutableLiveData()
    private val adapter = ProductsRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        productsList.observe(viewLifecycleOwner) {
            this.updateRecyclerView(it)
            onStopLoad()
        }
        ApiManager.setConnectCallback(requireContext(), object : IInternetConnected {
            override fun onConnect() {
                if (productsList.value.isNullOrEmpty()) {
                    onLoadContent()
                }
            }

            override fun onLost() {
                Snackbar.make(
                    binding.root,
                    "Отсутствует интернет соединение",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        })
        binding.etCart.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .add(
                    Properties.containerId,
                    CartFragment(),
                    CartFragment::javaClass.name
                )
                .addToBackStack(CartFragment::javaClass.name)
                .commitAllowingStateLoss()
        }
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
            onLoadContent()
        }
        binding.sneaksProducts.adapter = adapter
    }

    private fun onLoadContent() {
        onStartLoad()
        MainScope().launch(Dispatchers.IO) {
            productsList.postValue(ApiHelper.getProducts())
        }
    }

    private fun updateRecyclerView(products: List<ProductResponse>) {
        adapter.onUpdateList(products)
    }

    private fun onStartLoad() {
        binding.contentLoader.visibility = VISIBLE
        binding.sneaksProducts.visibility = GONE
    }

    private fun onStopLoad() {
        binding.contentLoader.visibility = GONE
        binding.sneaksProducts.visibility = VISIBLE
    }

    override fun onStop() {
        super.onStop()
        ApiManager.unsetConnectCallback()
        binding.etCart.setOnClickListener(null)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}