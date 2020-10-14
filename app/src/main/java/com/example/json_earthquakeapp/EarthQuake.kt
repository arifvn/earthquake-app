package com.example.json_earthquakeapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Result(
    val features: List<Feature>
)

data class Feature(
    val properties: EarthQuake
)

@Parcelize
data class EarthQuake(
    val mag: Double,
    val place: String,
    val time: Long,
    val url: String
) : Parcelable





