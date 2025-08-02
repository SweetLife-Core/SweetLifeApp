package com.amikom.sweetlife.data.remote.dto.minicourse

data class MiniCourseResponse(
    val data: MiniCourseData // Nama properti 'data' pertama
)

data class MiniCourseData(
    val data: List<MiniCourseDto> // Nama properti 'data' kedua
)

data class MiniCourseDto(
    val id: Int,
    val title: String,
    val image: String, // Ini akan menjadi URL gambar
    val url: String,   // Ini akan menjadi URL link artikel
    val description: String,
    val created_at: String,
    val updated_at: String
)