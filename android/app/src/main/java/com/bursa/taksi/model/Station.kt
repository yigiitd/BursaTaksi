package com.bursa.taksi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stations")
data class Station(
    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = false)
    val uid: Int,

    @ColumnInfo(name = "name")
    @Expose
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "latitude")
    @Expose
    @SerializedName("latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    @Expose
    @SerializedName("longitude")
    val longitude: Double,

    @ColumnInfo(name = "phone_number")
    @Expose
    @SerializedName("phone_number")
    val phoneNumber: String
)