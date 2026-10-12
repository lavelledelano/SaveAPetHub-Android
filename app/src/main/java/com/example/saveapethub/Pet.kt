package com.example.saveapethub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    @SerialName("pet_id") val petId: Long,
    val name: String,
    val species: String,
    val breed: String? = null,
    @SerialName("age_years") val ageYears: Int? = null,
    val colour: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val status: String = "Available"
)