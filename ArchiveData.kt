package com.example.finalproject

data class ArchiveData(
    var id: String = "",
    var title: String = "",
    var rating: Int = 0,
    var imageUrl: String = "",
    var category: String = "",
    var content: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
