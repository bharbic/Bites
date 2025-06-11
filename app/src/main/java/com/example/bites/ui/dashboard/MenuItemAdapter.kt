package com.example.bites.ui.adapters

import android.util.Log // Import Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bites.data.entity.MenuItemEntity
import com.example.bites.databinding.ItemMenuItemBinding // Your binding class name

class MenuAdapter(
    private val onItemClicked: (MenuItemEntity) -> Unit
) : ListAdapter<MenuItemEntity, MenuAdapter.MenuItemViewHolder>(MENU_ITEM_DIFF_CALLBACK) { // Use the separate object

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        Log.d("MenuAdapter", "onCreateViewHolder called") // LOG
        val binding = ItemMenuItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuItemViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.d("MenuAdapter", "onBindViewHolder for position $position, item: ${currentItem?.name}")

        currentItem?.let { menuItem ->
            holder.bind(menuItem) // Bind the data

            // REMOVE THIS (or comment it out) if you only want the button to be clickable:
            /*
            holder.itemView.setOnClickListener {
                Log.d("MenuAdapter", "ItemView clicked for: ${menuItem.name}") // Optional log
                onItemClicked(menuItem)
            }
            */

            // ADD THIS to make ONLY the "Add to Cart" button clickable:
            holder.binding.buttonAddToCart.setOnClickListener {
                Log.d("MenuAdapter", "buttonAddToCart clicked for: ${menuItem.name}") // Optional log
                onItemClicked(menuItem) // This 'menuItem' is the correct MenuItemEntity
            }
        }
    }

    class MenuItemViewHolder(
        val binding: ItemMenuItemBinding // Made public for access from onBindViewHolder if needed for button
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItemEntity) {
            Log.d("MenuItemViewHolder", "Binding item: ${menuItem.name}, Price: ${menuItem.price}, Desc: ${menuItem.description}") // LOG
            try {
                binding.textViewMenuItemName.text = menuItem.name
                binding.textViewMenuItemPrice.text = String.format("$%.2f", menuItem.price)
                binding.textViewMenuItemDescription.text = menuItem.description // Bind description

                // TODO: Handle image loading with a library like Coil or Glide
                // if (menuItem.imageUrl.isNotEmpty()) {
                //     binding.imageViewMenuItemPhoto.load(menuItem.imageUrl) {
                //         placeholder(R.drawable.placeholder_image) // Optional placeholder
                //         error(R.drawable.error_image) // Optional error image
                //     }
                // } else {
                //     binding.imageViewMenuItemPhoto.setImageResource(R.drawable.placeholder_image) // Default if no URL
                // }

            } catch (e: Exception) {
                Log.e("MenuItemViewHolder", "Error during bind for '${menuItem.name}': ${e.message}", e)
            }
        }
    }
}

// Define the DiffUtil.ItemCallback as a private top-level object or a private companion object within the adapter
private val MENU_ITEM_DIFF_CALLBACK = object : DiffUtil.ItemCallback<MenuItemEntity>() {
    override fun areItemsTheSame(oldItem: MenuItemEntity, newItem: MenuItemEntity): Boolean {
        return oldItem.itemID == newItem.itemID
    }

    override fun areContentsTheSame(oldItem: MenuItemEntity, newItem: MenuItemEntity): Boolean {
        return oldItem == newItem // Assumes MenuItemEntity is a data class or has well-defined equals()
    }
}