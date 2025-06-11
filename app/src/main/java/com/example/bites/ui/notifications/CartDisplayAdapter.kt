package com.example.bites.ui.notifications // Or your chosen package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bites.data.entity.CartDisplayItem // Ensure correct import
import com.example.bites.databinding.ItemCartDisplayBinding // Generated ViewBinding class

// Add a lambda to the constructor for the remove action
class CartDisplayAdapter(
    private val onRemoveClicked: (CartDisplayItem) -> Unit // Callback for remove action
) : ListAdapter<CartDisplayItem, CartDisplayAdapter.ViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
                    itemView.context.resources.configuration.locales[0],
                    "$%.2f x %d",
                    cartItem.menuItem.price,
                    cartItem.quantity
                )
            binding.textViewCartItemTotalPrice.text =
                String.format(
                    itemView.context.resources.configuration.locales[0],
                    "$%.2f",
                    cartItem.itemTotalPrice
                )

            // ***** SET ONCLICK LISTENER FOR REMOVE BUTTON *****
            binding.buttonRemoveItemFromCart.setOnClickListener {
                onRemoveClicked(cartItem) // Call the lambda passed to the adapter
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartDisplayItem>() {
        override fun areItemsTheSame(oldItem: CartDisplayItem, newItem: CartDisplayItem): Boolean {
            // Assuming menuItem.itemID is the unique, stable identifier for the menu item itself
            return oldItem.menuItem.itemID == newItem.menuItem.itemID
        }

        override fun areContentsTheSame(oldItem: CartDisplayItem, newItem: CartDisplayItem): Boolean {
            // Data class 'equals' compares all properties (menuItem and quantity)
            return oldItem == newItem
        }
    }
}