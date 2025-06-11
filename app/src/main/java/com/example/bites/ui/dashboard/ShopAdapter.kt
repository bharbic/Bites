package com.example.bites.ui.dashboard // Make sure this package name is correct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bites.R
import com.example.bites.data.entity.ShopEntity // Make sure this import is correct

class ShopAdapter(
    private val onItemClicked: (ShopEntity) -> Unit
) : ListAdapter<ShopEntity, ShopAdapter.ShopViewHolder>(ShopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false) // Ensure item_shop.xml exists in res/layout
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val shop = getItem(position)
        holder.bind(shop, onItemClicked)
    }

    class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val shopNameTextView: TextView = itemView.findViewById(R.id.textViewShopName)
        private val shopIconImageView: ImageView = itemView.findViewById(R.id.imageViewShopIconPlaceholder)

        fun bind(shop: ShopEntity, onItemClicked: (ShopEntity) -> Unit) {
            shopNameTextView.text = shop.name
            // Using a placeholder icon.
            shopIconImageView.setImageResource(android.R.drawable.ic_popup_reminder)

            itemView.setOnClickListener {
                onItemClicked(shop)
            }
        }
    }

    class ShopDiffCallback : DiffUtil.ItemCallback<ShopEntity>() {
        override fun areItemsTheSame(oldItem: ShopEntity, newItem: ShopEntity): Boolean {
            return oldItem.shopID == newItem.shopID // Use a unique identifier
        }

        override fun areContentsTheSame(oldItem: ShopEntity, newItem: ShopEntity): Boolean {
            return oldItem == newItem // Data class equality checks all properties
        }
    }
}