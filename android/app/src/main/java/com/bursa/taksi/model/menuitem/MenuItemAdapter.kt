package com.bursa.taksi.model.menuitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bursa.taksi.R
import com.bursa.taksi.databinding.ItemMenuBinding

class MenuItemAdapter(
    private val listener: OnItemClickListener
): RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    private var menuItemList = emptyList<MenuItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding =
            ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = menuItemList[position]

        holder.binding.apply {
            when (menuItem.type) {
                MenuItem.MenuItemTypes.Account -> {
                    icon.setImageResource(R.drawable.baseline_account)
                    text.setText(R.string.text_your_account)
                }
                MenuItem.MenuItemTypes.Options -> {
                    icon.setImageResource(R.drawable.baseline_settings)
                    text.setText(R.string.text_options)
                }
            }
        }

        holder.binding.root.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onClick(menuItem)
            }
        }
    }

    override fun getItemCount(): Int =
        menuItemList.size

    fun setData(newList: List<MenuItem>) {
        menuItemList = newList
        notifyDataSetChanged()
    }

    class MenuItemViewHolder(val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onClick(menuItem: MenuItem)
    }
}