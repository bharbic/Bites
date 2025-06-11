package com.example.bites.ui.notifications // Or your chosen package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bites.data.entity.CartDisplayItem // Ensure correct import
import com.example.bites.databinding.ItemCartDisplayBinding // Generated ViewBinding class
// If you plan to use image loading, uncomment and add Glide/Coil dependency
// import com.bumptech.glide.Glide
// import com.example.bites.R // For placeholder/error drawables if using Glide

class CartDisplayAdapter : ListAdapter<CartDisplayItem, CartDisplayAdapter.ViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout using ViewBinding
        val binding = ItemCartDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemCartDisplayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartDisplayItem) {
            binding.textViewCartItemName.text = cartItem.menuItem.name
            binding.textViewCartItemPriceAndQuantity.text =
                String.format(
                    itemView.context.resources.configuration.locales[0], // Use device locale for formatting
                    "$%.2f x %d",
                    cartItem.menuItem.price,
                    cartItem.quantity
                )
            binding.textViewCartItemTotalPrice.text =
                String.format(
                    itemView.context.resources.configuration.locales[0], // Use device locale
                    "$%.2f",
                    cartItem.itemTotalPrice
                )

            // Optional: Load image using Glide (or Coil, Picasso)
            // Make sure you have the Glide dependency in your build.gradle if you use this.
            // if (cartItem.menuItem.imageUrl != null) {
            //     Glide.with(binding.imageViewCartItem.context)
            //         .load(cartItem.menuItem.imageUrl)
            //         .placeholder(R.drawable.your_placeholder_image) // Create a placeholder drawable
            //         .error(R.drawable.your_error_image) // Create an error drawable
            //         .into(binding.imageViewCartItem)
            // } else {
            //     // Set a default image or hide the ImageView if no URL
            //     binding.imageViewCartItem.setImageResource(R.drawable.your_default_image)
            // }

            // If you removed the ImageView from XML, you don't need the Glide part.
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartDisplayItem>() {
        override fun areItemsTheSame(oldItem: CartDisplayItem, newItem: CartDisplayItem): Boolean {
            // Check if the items represent the same underlying entity
            return oldItem.menuItem.itemID == newItem.menuItem.itemID
        }

        override fun areContentsTheSame(oldItem: CartDisplayItem, newItem: CartDisplayItem): Boolean {
            // Check if all contents of the item are the same (name, price, quantity, etc.)
            // The default data class 'equals' implementation works well here if CartDisplayItem
            // and MenuItemEntity are data classes.
            return oldItem == newItem
        }
    }
}