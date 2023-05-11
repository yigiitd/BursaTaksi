package com.bursa.taksi.model.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bursa.taksi.databinding.ItemLocationBinding

class LocationAdapter(
    private val listener: OnItemClickListener
): RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var locationList = emptyList<LocationPrediction>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationViewHolder {
        val binding =
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]

        holder.binding.apply {
            textPrimary.text = location.primaryText
            textSecondary.text = location.secondaryText
        }

        holder.binding.root.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onClick(location)
            }
        }
    }

    override fun getItemCount(): Int =
        locationList.size


    fun setData(newList: List<LocationPrediction>) {
        locationList = newList
        notifyDataSetChanged()
        // DiffUtil doesn't work properly
        // Temporarily using notifyDataSetChanged()
        // TODO
    }

    class LocationViewHolder(val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onClick(location: LocationPrediction)
    }
}