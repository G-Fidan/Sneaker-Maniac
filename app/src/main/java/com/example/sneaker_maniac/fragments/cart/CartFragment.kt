package com.example.sneaker_maniac.fragments.cart

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.sneaker_maniac.App
import com.example.sneaker_maniac.R
import com.example.sneaker_maniac.api.ApiHelper
import com.example.sneaker_maniac.api.ApiManager
import com.example.sneaker_maniac.api.IInternetConnected
import com.example.sneaker_maniac.api.models.ProductCart
import com.example.sneaker_maniac.api.models.ProductResponse
import com.example.sneaker_maniac.databinding.FragmentCartBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private var productsList: MutableLiveData<List<ProductResponse>> = MutableLiveData()
    private val carts: MutableList<ProductCart> = mutableListOf()
    private lateinit var adapter: CartRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = CartRecyclerAdapter(
            object : ICartItemClick {
                override fun onItemClick(position: Int, mode: Int) {
                    val prefsEditor: SharedPreferences.Editor =
                        requireActivity().getSharedPreferences(
                            App.appContext.getString(R.string.app_name),
                            Context.MODE_PRIVATE
                        ).edit()

                    if (mode > 0) {
                        carts[position].count = carts[position].count?.plus(1)
                        val json = Gson().toJson(carts[position])
                        prefsEditor.putString(carts[position].product?.name, json)
                    } else {
                        if (carts[position].count!! > 1) {
                            carts[position].count = carts[position].count?.minus(1)
                            val json = Gson().toJson(carts[position])
                            prefsEditor.putString(carts[position].product?.name, json)
                        } else {
                            prefsEditor.remove(carts[position].product?.name)
                            carts.removeAt(position)
                        }
                    }
                    prefsEditor.apply()
                    updateRecyclerView(carts)
                }
            },
        )
        binding.sneaksCarts.adapter = adapter

        productsList.observe(viewLifecycleOwner) {
            onStopLoad()
            it.map { item ->
                val model = requireActivity().getSharedPreferences(
                    App.appContext.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                ).getString(item.name, "")
                if (model != "") {
                    carts.add(
                        Gson().fromJson(
                            model,
                            ProductCart::class.java
                        )
                    )
                }
            }

            updateRecyclerView(carts)
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
    }

    private fun onLoadContent() {
        onStartLoad()
        MainScope().launch(Dispatchers.IO) {
            productsList.postValue(ApiHelper.getProducts())
        }
    }

    private fun updateRecyclerView(products: List<ProductCart>) {
        adapter.onUpdateList(products)
    }

    private fun onStartLoad() {
        binding.contentLoader.visibility = View.VISIBLE
        binding.sneaksCarts.visibility = View.GONE
    }

    private fun onStopLoad() {
        binding.contentLoader.visibility = View.GONE
        binding.sneaksCarts.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}