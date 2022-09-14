package com.example.universitysystem

data class FeedRecord(
    val author: String,
    val authorIdChat: String,
    val time: String,
    val record_text: String,
    val isSponsored: Boolean,
    val record: Long,
    val userId: String
)
