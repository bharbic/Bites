package com.example.bites.ui.profile // Or your chosen package for adapter

import android.view.LayoutInflater
import android.view.View // Import View for visibility changes
import android.view.ViewGroup
import androidx.compose.ui.semantics.text

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bites.data.entity.OrderEntity
import com.example.bites.databinding.ItemOrderHistoryBinding
// We won't need SimpleDateFormat, Date, or Locale for this simplified version

class OrderHistoryAdapter(private val onItemClicked: (OrderEntity) -> Unit) :
    ListAdapter<OrderEntity, OrderHistoryAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
        holder.itemView.setOnClickListener {
            onItemClicked(order)
        }
    }

    inner class OrderViewHolder(private val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderEntity) {
            // Display existing information
            binding.textViewOrderIdValue.text = order.orderID.toString()
            binding.textViewOrderStatusValue.text = order.deliveryStatus // Or whatever status field you have

            // --- Handling Date and Total TextViews ---

            // Option A: Hide the Date label and value if not available
            binding.textViewOrderDateLabel.visibility = View.GONE
            binding.textViewOrderDateValue.visibility = View.GONE

            // Option B: Show "N/A" if you prefer to keep the space
            // binding.textViewOrderDateValue.text = "N/A"
            // binding.textViewOrderDateLabel.visibility = View.VISIBLE // Keep label visible
            // binding.textViewOrderDateValue.visibility = View.VISIBLE // Keep value visible


            // Option A: Hide the Total label and value if not available
            binding.textViewOrderTotalLabel.visibility = View.GONE
            binding.textViewOrderTotalValue.visibility = View.GONE

            // Option B: Show "N/A" if you prefer to keep the space
            // binding.textViewOrderTotalValue.text = "N/A"
            // binding.textViewOrderTotalLabel.visibility = View.VISIBLE // Keep label visible
            // binding.textViewOrderTotalValue.visibility = View.VISIBLE // Keep value visible

            // You can add more existing fields from OrderEntity here if you have them in your item_order_history.xml
            // For example, if you had a TextView for special requests:
            // if (order.specialRequest.isNullOrBlank()) {
            //     binding.textViewSpecialRequestLabel.visibility = View.GONE
            //     binding.textViewSpecialRequestValue.visibility = View.GONE
            // } else {
            //     binding.textViewSpecialRequestLabel.visibility = View.VISIBLE
            //     binding.textViewSpecialRequestValue.visibility = View.VISIBLE
            //     binding.textViewSpecialRequestValue.text = order.specialRequest
            // }
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<OrderEntity>() {
    override fun areItemsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean {
        return oldItem.orderID == newItem.orderID
    }

    override fun areContentsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean {
        return oldItem == newItem // If OrderEntity is a data class, this works fine
    }
}