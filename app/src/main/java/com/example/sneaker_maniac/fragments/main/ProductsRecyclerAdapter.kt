package com.example.sneaker_maniac.fragments.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sneaker_maniac.R
import com.example.sneaker_maniac.api.models.ProductResponse

interface IProductsRecyclerAdapter {
    fun onUpdateList(list: List<ProductResponse>)
}

class ProductsRecyclerAdapter(
    private val delegate: IItemClick
) : RecyclerView.Adapter<ProductsRecyclerAdapter.ProductsViewHolder>(),
    IProductsRecyclerAdapter {

    private var products: List<ProductResponse> = listOf()

    class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val iconSneak: AppCompatImageView = itemView.findViewById(R.id.sneakIcon)
        val nameSneak: AppCompatTextView = itemView.findViewById(R.id.sneakPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.sneak_cell, parent, false)
        return ProductsViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            delegate.onItemClick(position)
        }
        holder.nameSneak.text = products[position].name + "(" + products[position].price + "руб.)"
    }

    override fun getItemCount() = products.size

    override fun onUpdateList(list: List<ProductResponse>) {
        products = list
        this.notifyDataSetChanged()
    }
}