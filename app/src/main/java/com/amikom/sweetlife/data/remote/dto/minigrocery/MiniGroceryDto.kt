package com.amikom.sweetlife.data.remote.dto.minigrocery

import com.google.gson.annotations.SerializedName

data class GroceryResponse(
    val data: GroceryData, // Properti 'data' pertama
    val status: Boolean
)

data class GroceryData(
    val data: List<GroceryItemDto>, // Properti 'data' kedua, isinya List dari item
    val pagination: PaginationDto // Tambahkan kelas untuk pagination
)

// >>> KELAS UNTUK 'data' KEDUA (ITEM-ITEMNYA) <<<
data class GroceryItemDto(
    val id: Int,
    val title: String,
    val image: String,
    val url: String,
    val description: String,
    val price: Int, // Tambahkan properti 'price'
    @SerializedName("created_at") // Gunakan ini jika nama properti API ada underscore
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

// >>> KELAS UNTUK 'pagination' <<<
data class PaginationDto(
    val page: Int,
    val limit: Int,
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("has_next")
    val hasNext: Boolean,
    @SerializedName("has_prev")
    val hasPrev: Boolean
)