package com.bursa.taksi.model.location

import androidx.recyclerview.widget.DiffUtil

class LocationDiffUtil (
    private val oldList: List<LocationPrediction>,
    private val newList: List<LocationPrediction>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int =
        oldList.size

    override fun getNewListSize(): Int =
        newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].placeId == newList[newItemPosition].placeId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}