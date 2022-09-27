package com.example.sneaker_maniac.fragments.main

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.sneaker_maniac.App
import com.example.sneaker_maniac.Properties
import com.example.sneaker_maniac.R
import com.example.sneaker_maniac.api.ApiHelper
import com.example.sneaker_maniac.api.ApiManager
import com.example.sneaker_maniac.api.IInternetConnected
import com.example.sneaker_maniac.api.models.ProductCart
import com.example.sneaker_maniac.api.models.ProductResponse
import com.example.sneaker_maniac.databinding.FragmentMainBinding
import com.example.sneaker_maniac.fragments.cart.CartFragment
import com.example.sneaker_maniac.fragments.profile.ProfileFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var productsList: MutableLiveData<List<ProductResponse>> = MutableLiveData()
    private var countInCart: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var adapter: ProductsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        adapter = ProductsRecyclerAdapter(object : IItemClick {
            override fun onItemClick(position: Int) {
                Snackbar.make(
                    binding.root,
                    productsList.value?.get(position)?.name.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()

                val isCheck = requireActivity().getSharedPreferences(
                    App.appContext.getString(R.string.app_name),
                    MODE_PRIVATE
                )
                    .getString(productsList.value?.get(position)?.name, "")
                val prefsEditor: SharedPreferences.Editor =
                    requireActivity().getSharedPreferences(
                        App.appContext.getString(R.string.app_name),
                        MODE_PRIVATE
                    ).edit()

                if (isCheck == "") {
                    val json = Gson().toJson(
                        ProductCart(
                            id = productsList.value?.get(position)?.id,
                            product = productsList.value?.get(position),
                            count = 1
                        )
                    )
                    prefsEditor.putString(productsList.value?.get(position)?.name, json)
                } else {
                    val json = Gson().toJson(
                        ProductCart(
                            id = productsList.value?.get(position)?.id,
                            product = Gson().fromJson(isCheck, ProductCart::class.java).product,
                            count = Gson().fromJson(isCheck, ProductCart::class.java).count?.plus(1)
                        )
                    )
                    prefsEditor.putString(productsList.value?.get(position)?.name, json)
                }
                prefsEditor.apply()

                countInCart.value = countInCart.value?.plus(1)
            }
        })
        binding.sneaksProducts.adapter = adapter
        productsList.observe(viewLifecycleOwner) {
            this.updateRecyclerView(it)
            onStopLoad()

            countInCart.value = 0
            it.map { item ->
                val model = requireActivity().getSharedPreferences(
                    App.appContext.getString(R.string.app_name),
                    MODE_PRIVATE
                ).getString(item.name, "")
                if (model != "") {
                    countInCart.value = countInCart.value?.plus(
                        Gson().fromJson(
                            model,
                            ProductCart::class.java
                        ).count!!
                    )
                }
            }
        }
        countInCart.observe(viewLifecycleOwner) {
            if (it == 0) {
                binding.tvCart.text = "Корзина: Нет товаров."
            } else {
                binding.tvCart.text = "Корзина: $it товаров."
            }
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
        binding.tvCart.setOnClickListener {
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
        binding.btnProfile.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .add(
                    Properties.containerId,
                    ProfileFragment(),
                    ProfileFragment::javaClass.name
                )
                .addToBackStack(ProfileFragment::javaClass.name)
                .commitAllowingStateLoss()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                productsList.value?.filter { item -> item.name?.contains(p0.toString()) == true }
                    ?.toList()
                    ?.let { updateRecyclerView(it) }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
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
        binding.tvCart.setOnClickListener(null)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}