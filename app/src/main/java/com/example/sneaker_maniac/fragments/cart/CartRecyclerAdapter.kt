package com.example.sneaker_maniac.fragments.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sneaker_maniac.R
import com.example.sneaker_maniac.api.models.ProductCart

interface ICartRecyclerAdapter {
    fun onUpdateList(carts: List<ProductCart>)
}

class CartRecyclerAdapter(
    private val delegate: ICartItemClick
) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(),
    ICartRecyclerAdapter {

    private var cartList: List<ProductCart> = listOf()

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countMinus: AppCompatTextView = itemView.findViewById(R.id.countMinus)
        val countPlus: AppCompatTextView = itemView.findViewById(R.id.countPlus)
        val nameSneak: AppCompatTextView = itemView.findViewById(R.id.sneakName)
        val countSneak: AppCompatTextView = itemView.findViewById(R.id.sneakCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.sneak_cart_cell, parent, false)
        return CartViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.countMinus.setOnClickListener {
            delegate.onItemClick(position, -1)
        }
        holder.countPlus.setOnClickListener {
            delegate.onItemClick(position, 1)
        }
        holder.nameSneak.text =
            cartList[position].product?.name + "(" + cartList[position].product?.price + "руб.)"
        holder.countSneak.text = cartList[position].count.toString()
    }

    override fun getItemCount() = cartList.size

    override fun onUpdateList(carts: List<ProductCart>) {
        cartList = carts
        this.notifyDataSetChanged()
    }
}